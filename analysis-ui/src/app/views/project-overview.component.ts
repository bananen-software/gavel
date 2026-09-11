import {ChangeDetectionStrategy, Component, computed, inject, input} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NzTableModule} from 'ng-zorro-antd/table';
import {NzTooltipModule} from 'ng-zorro-antd/tooltip';
import {ComplexityMixComponent} from '../charts/complexity-mix.component';
import {MainSequenceComponent} from '../charts/main-sequence.component';
import type {PackageSummary} from '../api/schema.types';
import {count, decimal, num, percent} from '../shared/format';
import {humanise, packageComplexityConcern, STRATUM_ORDER, stratumColor,} from '../shared/ratings';
import {EmptyComponent, MetricComponent, PanelComponent, RatingComponent} from '../shared/ui.components';
import {AnalysisStore} from '../state/analysis.store';

@Component({
    selector: 'app-project-overview',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterLink,
        NzTableModule,
        NzTooltipModule,
        MainSequenceComponent,
        ComplexityMixComponent,
        MetricComponent,
        PanelComponent,
        RatingComponent,
        EmptyComponent,
    ],
    template: `
        @if (store.packages().length === 0 && !store.snapshotLoading()) {
            <app-empty
                    headline="This project has no analysed packages"
                    detail="Run the analysis on the backend, then reload."
            />
        } @else {
            <div class="metrics">
                <app-metric label="Lines of code" [value]="count(totals().linesOfCode)"/>
                <app-metric
                        label="Types"
                        [value]="count(totals().types)"
                        [hint]="count(totals().packages) + ' packages'"
                />
                <app-metric
                        label="Findings"
                        [value]="count(totals().findings)"
                        [hint]="count(totals().highPriorityFindings) + ' high priority'"
                />
                <app-metric
                        label="Comment ratio"
                        [value]="percent(totals().commentToCodeRatio)"
                        [hint]="count(totals().linesOfComments) + ' comment lines'"
                />
            </div>

            <div class="section-grid">
                <app-panel
                        heading="Main sequence"
                        note="abstractness against instability; size is type count"
                >
                    <app-main-sequence [packages]="store.packages()" (select)="openPackage($event.id)"/>
                </app-panel>

                <app-panel heading="Complexity mix" note="top 10 packages by high-complexity types">
                    <app-complexity-mix [packages]="store.packages()"/>
                </app-panel>
            </div>

            <app-panel heading="Packages" [note]="count(store.packages().length) + ' analysed'" [flush]="true">
                <nz-table
                        #table
                        [nzData]="store.packages()"
                        nzSize="small"
                        [nzPageSize]="25"
                        [nzShowSizeChanger]="true"
                        [nzScroll]="{ x: '1000px' }"
                >
                    <thead>
                    <tr>
                        <th nzWidth="280px" [nzSortFn]="byName">Package</th>
                        <th nzWidth="130px" [nzSortFn]="byComplexityRating" [nzSortOrder]="sortOrder">Rating</th>
                        <th nzWidth="80px" nzAlign="right" [nzSortFn]="byTypes">Types</th>
                        <th nzWidth="90px" nzAlign="right" [nzSortFn]="byLoc">LOC</th>
                        <th nzWidth="110px" nzAlign="right" [nzSortFn]="byDefectDensity">
                            <span nz-tooltip nzTooltipTitle="Findings per 1k lines of code">Defects / KLOC</span>
                        </th>
                        <th nzWidth="90px" nzAlign="right" [nzSortFn]="byFindings">Findings</th>
                        <th nzWidth="110px" nzAlign="right" [nzSortFn]="byDistance">
                            <span nz-tooltip nzTooltipTitle="Distance from the main sequence">Distance</span>
                        </th>
                        <th nzWidth="120px" [nzFilters]="stratumFilters" [nzFilterFn]="byStratum">Stratum</th>
                    </tr>
                    </thead>
                    <tbody>
                        @for (pkg of table.data; track pkg.id) {
                            <tr (click)="openPackage(pkg.id)">
                                <td>
                                    <a
                                            class="identifier"
                                            [routerLink]="['/projects', projectId(), 'packages', pkg.id]"
                                            [title]="pkg.name ?? ''"
                                    >{{ pkg.name ?? 'unnamed' }}</a
                                    >
                                </td>
                                <td>
                                    <app-rating
                                            [label]="humanise(pkg.complexityRating)"
                                            [concern]="packageComplexityConcern(pkg.complexityRating)"
                                    />
                                </td>
                                <td class="numeric">{{ count(pkg.numberOfTypes) }}</td>
                                <td class="numeric">{{ count(pkg.linesOfCode) }}</td>
                                <td class="numeric">{{ decimal(pkg.defectDensity, 3) }}</td>
                                <td class="numeric">{{ count(pkg.totalNumberOfFindings) }}</td>
                                <td class="numeric">{{ decimal(pkg.componentDependency?.distance) }}</td>
                                <td>
                  <span class="stratum" [style.color]="stratumColor(pkg.stratum)">
                    {{ humanise(pkg.stratum, 'None') }}
                  </span>
                                </td>
                            </tr>
                        }
                    </tbody>
                </nz-table>
            </app-panel>
        }
    `,
    styles: [
        `
            :host {
                display: grid;
                gap: 16px;
            }

            .metrics {
                display: grid;
                gap: 12px;
                grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
            }

            .stratum {
                font-size: 12px;
                font-weight: 500;
            }

            td a {
                color: var(--ink);
            }

            td a:hover {
                color: var(--accent);
            }
        `,
    ],
})
export class ProjectOverviewComponent {
    readonly projectId = input.required<string>();

    protected sortOrder = 'descending';

    protected readonly store = inject(AnalysisStore);
    private readonly router = inject(Router);

    protected readonly totals = computed(() => this.store.totals());

    protected readonly count = count;
    protected readonly decimal = decimal;
    protected readonly humanise = humanise;
    protected readonly packageComplexityConcern = packageComplexityConcern;
    protected readonly stratumColor = stratumColor;

    protected readonly stratumFilters = STRATUM_ORDER.map((value) => ({
        text: humanise(value),
        value,
    }));

    protected readonly byName = (a: PackageSummary, b: PackageSummary): number =>
        (a.name ?? '').localeCompare(b.name ?? '');
    protected readonly byComplexity = (a: PackageSummary, b: PackageSummary): number =>
        num(a.complexity) - num(b.complexity);
    protected readonly byComplexityRating = (a: PackageSummary, b: PackageSummary): number =>
        num(a.complexityOrdinal) - num(b.complexityOrdinal);
    protected readonly byTypes = (a: PackageSummary, b: PackageSummary): number =>
        num(a.numberOfTypes) - num(b.numberOfTypes);
    protected readonly byLoc = (a: PackageSummary, b: PackageSummary): number =>
        num(a.linesOfCode) - num(b.linesOfCode);
    protected readonly byDefectDensity = (a: PackageSummary, b: PackageSummary): number =>
        num(a.defectDensity) - num(b.defectDensity);
    protected readonly byFindings = (a: PackageSummary, b: PackageSummary): number =>
        num(a.totalNumberOfFindings) - num(b.totalNumberOfFindings);
    protected readonly byDistance = (a: PackageSummary, b: PackageSummary): number =>
        num(a.componentDependency?.distance) - num(b.componentDependency?.distance);
    protected readonly byStratum = (list: string[], item: PackageSummary): boolean =>
        list.some((value) => value === (item.stratum ?? 'NONE'));

    protected openPackage(packageId: string): void {
        void this.router.navigate(['/projects', this.projectId(), 'packages', packageId]);
    }

    protected readonly percent = percent;
}
