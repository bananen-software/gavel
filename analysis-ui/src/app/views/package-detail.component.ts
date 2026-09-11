import {ChangeDetectionStrategy, Component, computed, effect, inject, input, untracked} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NzAlertModule} from 'ng-zorro-antd/alert';
import {NzSpinModule} from 'ng-zorro-antd/spin';
import {NzTableModule} from 'ng-zorro-antd/table';
import {HotspotChartComponent} from '../charts/hotspot-chart.component';
import type {ClassSummary} from '../api/schema.types';
import {count, decimal, formatDate, num, percent, simpleClassName} from '../shared/format';
import {
    classComplexityConcern,
    cohesionConcern,
    humanise,
    packageComplexityConcern,
    stratumColor,
} from '../shared/ratings';
import {EmptyComponent, MetricComponent, PanelComponent, RatingComponent,} from '../shared/ui.components';
import {AnalysisStore} from '../state/analysis.store';

@Component({
    selector: 'app-package-detail',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterLink,
        NzAlertModule,
        NzSpinModule,
        NzTableModule,
        HotspotChartComponent,
        MetricComponent,
        PanelComponent,
        RatingComponent,
        EmptyComponent,
    ],
    template: `
        @if (pkg(); as p) {
            <header class="crumb">
                <a [routerLink]="['/projects', projectId()]">All packages</a>
                <span class="sep">›</span>
                <span class="identifier">{{ p.name ?? 'unnamed' }}</span>
                <app-rating
                        [label]="humanise(p.complexityRating)"
                        [concern]="packageComplexityConcern(p.complexityRating)"
                />
                <span class="stratum" [style.color]="stratumColor(p.stratum)">
          {{ humanise(p.stratum, 'None') }} layer
        </span>
            </header>

            <div class="metrics">
                <app-metric label="Complexity" [value]="count(p.complexity)"/>
                <app-metric
                        label="Types"
                        [value]="count(p.numberOfTypes)"
                        [hint]="humanise(p.size, 'Unknown') + ' package'"
                />
                <app-metric
                        label="Lines of code"
                        [value]="count(p.linesOfCode)"
                        [hint]="'comment ratio ' + percent(p.commentToCodeRatio)"
                />
                <app-metric
                        label="Findings"
                        [value]="count(p.totalNumberOfFindings)"
                        [hint]="count(p.numberOfHighPriorityFindings) + ' high priority'"
                />
            </div>

            <div class="section-grid">
                <app-panel heading="Structure">
                    <dl>
                        <dt>Cohesion</dt>
                        <dd>
                            @if (p.relationalCohesion; as rc) {
                                <app-rating
                                        [label]="humanise(rc.rating)"
                                        [concern]="cohesionConcern(rc.rating)"
                                />
                                <span class="muted">
                  {{ decimal(rc.relationalCohesion) }} internal relationships per type
                  ({{ count(rc.numberOfInternalRelationships) }} across
                                    {{ count(rc.numberOfTypes) }} types)
                </span>
                            } @else {
                                <span class="muted">not measured</span>
                            }
                        </dd>

                        <dt>Coupling</dt>
                        <dd>
                            @if (p.componentDependency; as cd) {
                                <span class="muted">
                  {{ count(cd.afferentCoupling) }} packages depend on this one; it depends on
                                    {{ count(cd.efferentCoupling) }}.
                </span>
                            } @else {
                                <span class="muted">not measured</span>
                            }
                        </dd>

                        <dt>Main sequence</dt>
                        <dd>
                            @if (p.componentDependency; as cd) {
                                <span class="muted">
                  Instability {{ decimal(cd.instability) }}, abstractness
                                    {{ decimal(cd.abstractness) }} — {{ decimal(cd.distance) }} from the ideal line.
                </span>
                            } @else {
                                <span class="muted">not measured</span>
                            }
                        </dd>

                        <dt>Defect density</dt>
                        <dd>
              <span class="muted">
                {{ decimal(p.defectDensity, 3) }} findings per KLOC,
                  {{ decimal(p.highDefectDensity, 3) }} for high priority only.
              </span>
                        </dd>
                    </dl>
                </app-panel>

                <app-panel heading="Hotspots" note="changes against complexity; size is lines of code">
                    @if (classes().loading) {
                        <div class="centre">
                            <nz-spin nzSimple/>
                        </div>
                    } @else if (classes().value?.length) {
                        <app-hotspot-chart [classes]="classes().value ?? []" (select)="openClass($event)"/>
                    } @else {
                        <app-empty headline="No classes in this package"/>
                    }
                </app-panel>
            </div>

            <app-panel heading="Classes" [note]="count(classes().value?.length ?? 0) + ' in package'" [flush]="true">
                @if (classes().error; as error) {
                    <nz-alert nzType="error" nzMessage="Couldn't load classes" [nzDescription]="error"/>
                } @else if (classes().loading) {
                    <div class="centre">
                        <nz-spin nzSimple/>
                    </div>
                } @else {
                    <nz-table
                            #table
                            [nzData]="classes().value ?? []"
                            nzSize="small"
                            [nzPageSize]="30"
                            [nzShowSizeChanger]="true"
                            [nzScroll]="{ x: '1040px' }"
                    >
                        <thead>
                        <tr>
                            <th nzWidth="260px" [nzSortFn]="byName">Class</th>
                            <th nzWidth="120px" [nzSortFn]="byComplexity" [nzSortOrder]="sortOrder">Rating</th>
                            <th nzWidth="100px" nzAlign="right" [nzSortFn]="byComplexityValue">Complexity</th>
                            <th nzWidth="90px" nzAlign="right" [nzSortFn]="byLoc">Lines</th>
                            <th nzWidth="90px" nzAlign="right" [nzSortFn]="byChanges">Changes</th>
                            <th nzWidth="90px" nzAlign="right" [nzSortFn]="byAuthors">Authors</th>
                            <th nzWidth="110px" nzAlign="right" [nzSortFn]="byResponsibilities">
                                Responsibilities
                            </th>
                            <th nzWidth="90px" nzAlign="right" [nzSortFn]="byFindings">Findings</th>
                            <th nzWidth="120px" [nzSortFn]="byModified">Last change</th>
                        </tr>
                        </thead>
                        <tbody>
                            @for (cls of table.data; track cls.id) {
                                <tr (click)="openClass(cls.id)">
                                    <td>
                                        <a class="identifier"
                                           [title]="cls.name ?? ''">{{ simpleClassName(cls.name) }}</a>
                                        @if (cls.status === 'DELETED') {
                                            <span class="deleted">deleted</span>
                                        }
                                    </td>
                                    <td>
                                        <app-rating
                                                [label]="humanise(cls.complexityRating)"
                                                [concern]="classComplexityConcern(cls.complexityRating)"
                                        />
                                    </td>
                                    <td class="numeric">{{ count(cls.complexity) }}</td>
                                    <td class="numeric">{{ count(cls.totalLinesOfCode) }}</td>
                                    <td class="numeric">{{ count(cls.numberOfChanges) }}</td>
                                    <td class="numeric">{{ count(cls.numberOfAuthors) }}</td>
                                    <td class="numeric">{{ count(cls.numberOfResponsibilities) }}</td>
                                    <td class="numeric">{{ count(cls.totalNumberOfFindings) }}</td>
                                    <td class="meta">{{ formatDate(cls.lastModified) }}</td>
                                </tr>
                            }
                        </tbody>
                    </nz-table>
                }
            </app-panel>
        } @else if (!store.snapshotLoading()) {
            <app-empty
                    headline="Package not found"
                    detail="It may belong to another project. Pick one from the rail."
            />
        }
    `,
    styles: [
        `
            :host {
                display: grid;
                gap: 16px;
            }

            .crumb {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .crumb .identifier {
                font-size: 14px;
                font-weight: 500;
            }

            .sep {
                color: var(--ink-3);
            }

            .stratum {
                font-size: 12px;
                font-weight: 500;
            }

            .metrics {
                display: grid;
                gap: 12px;
                grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
            }

            dl {
                margin: 0;
                display: grid;
                grid-template-columns: 130px minmax(0, 1fr);
                gap: 10px 16px;
                align-items: baseline;
            }

            dt {
                color: var(--ink-2);
                font-size: 13px;
            }

            dd {
                margin: 0;
                display: flex;
                flex-wrap: wrap;
                align-items: baseline;
                gap: 8px;
                font-size: 13px;
            }

            .deleted {
                margin-left: 6px;
                font-size: 11px;
                color: var(--rate-none);
            }

            td a {
                color: var(--ink);
            }

            td a:hover {
                color: var(--accent);
            }

            .centre {
                display: flex;
                justify-content: center;
                padding: 32px;
            }
        `,
    ],
})
export class PackageDetailComponent {
    readonly projectId = input.required<string>();
    readonly packageId = input.required<string>();

    protected readonly store = inject(AnalysisStore);
    private readonly router = inject(Router);

    protected sortOrder = "descending";

    protected readonly pkg = computed(() => this.store.packageById(this.packageId())());
    protected readonly classes = computed(() => this.store.classesFor(this.packageId())());

    protected readonly count = count;
    protected readonly decimal = decimal;
    protected readonly formatDate = formatDate;
    protected readonly humanise = humanise;
    protected readonly simpleClassName = simpleClassName;
    protected readonly classComplexityConcern = classComplexityConcern;
    protected readonly packageComplexityConcern = packageComplexityConcern;
    protected readonly cohesionConcern = cohesionConcern;
    protected readonly stratumColor = stratumColor;

    protected readonly byName = (a: ClassSummary, b: ClassSummary): number =>
        (a.name ?? '').localeCompare(b.name ?? '');
    protected readonly byComplexity = (a: ClassSummary, b: ClassSummary): number =>
        num(a.complexity) - num(b.complexity);
    protected readonly byComplexityValue = this.byComplexity;
    protected readonly byLoc = (a: ClassSummary, b: ClassSummary): number =>
        num(a.totalLinesOfCode) - num(b.totalLinesOfCode);
    protected readonly byChanges = (a: ClassSummary, b: ClassSummary): number =>
        num(a.numberOfChanges) - num(b.numberOfChanges);
    protected readonly byAuthors = (a: ClassSummary, b: ClassSummary): number =>
        num(a.numberOfAuthors) - num(b.numberOfAuthors);
    protected readonly byResponsibilities = (a: ClassSummary, b: ClassSummary): number =>
        num(a.numberOfResponsibilities) - num(b.numberOfResponsibilities);
    protected readonly byFindings = (a: ClassSummary, b: ClassSummary): number =>
        num(a.totalNumberOfFindings) - num(b.totalNumberOfFindings);
    protected readonly byModified = (a: ClassSummary, b: ClassSummary): number =>
        (a.lastModified ?? '').localeCompare(b.lastModified ?? '');

    constructor() {
        effect(() => {
            const id = this.packageId();
            untracked(() => this.store.loadClasses(id));
        });
    }

    protected openClass(classId: string): void {
        void this.router.navigate([
            '/projects',
            this.projectId(),
            'packages',
            this.packageId(),
            'classes',
            classId,
        ]);
    }

    protected readonly percent = percent;
}
