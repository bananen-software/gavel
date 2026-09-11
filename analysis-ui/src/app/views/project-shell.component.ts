import {ChangeDetectionStrategy, Component, computed, effect, inject, input, signal, untracked} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {NzAlertModule} from 'ng-zorro-antd/alert';
import {NzInputModule} from 'ng-zorro-antd/input';
import {NzSelectModule} from 'ng-zorro-antd/select';
import {NzSpinModule} from 'ng-zorro-antd/spin';
import type {PackageSummary} from '../api/schema.types';
import {count, decimal, formatDateTime, num, shortPackageName} from '../shared/format';
import {
  analysisStatusConcern,
  concernColor,
  humanise,
  isAnalysisInProgress,
  packageComplexityConcern,
} from '../shared/ratings';
import {RatingComponent} from '../shared/ui.components';
import {AnalysisStore} from '../state/analysis.store';

type RailMetric = 'complexity' | 'linesOfCode' | 'defectDensity' | 'findings' | 'distance';

interface RailItem {
    pkg: PackageSummary;
    value: number;
    display: string;
    fraction: number;
    color: string;
}

/**
 * The rail is the idea this layout is built around.
 *
 * It is not a folder tree. It is a list ranked by whatever metric the user
 * picks, with a proportional bar behind each name, so navigating and answering
 * "where are the problems" are the same gesture. Package names share long
 * prefixes, so the name truncates from the left and the distinguishing tail
 * stays visible.
 */
@Component({
    selector: 'app-project-shell',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterOutlet,
        RouterLink,
        RouterLinkActive,
        FormsModule,
        NzAlertModule,
        NzInputModule,
        NzSelectModule,
        NzSpinModule,
        RatingComponent,
    ],
    template: `
        <header class="topbar">
            <a class="home" routerLink="/">Code analysis</a>
            <span class="sep">/</span>
            <span class="project identifier">{{ store.project()?.name ?? 'loading…' }}</span>
            @if (store.project(); as project) {
                <app-rating
                        [label]="humanise(project.analysisStatus)"
                        [concern]="analysisStatusConcern(project.analysisStatus)"
                />
                <span class="meta right">analysed {{ formatDateTime(project.lastAnalyzed) }}</span>
            }
        </header>

        @if (inProgress()) {
            <nz-alert
                    nzType="info"
                    nzBanner
                    nzMessage="Analysis is still running. Numbers will change as it completes."
            />
        }

        <div class="frame">
            <nav class="rail" aria-label="Packages">
                <div class="controls">
                    <input
                            nz-input
                            type="search"
                            placeholder="Filter packages"
                            [ngModel]="filter()"
                            (ngModelChange)="filter.set($event)"
                    />
                    <nz-select
                            [ngModel]="metric()"
                            (ngModelChange)="metric.set($event)"
                            nzSize="small"
                            class="metric-select"
                    >
                        <nz-option nzValue="complexity" nzLabel="Complexity"/>
                        <nz-option nzValue="linesOfCode" nzLabel="Lines of code"/>
                        <nz-option nzValue="defectDensity" nzLabel="Defect density"/>
                        <nz-option nzValue="findings" nzLabel="Findings"/>
                        <nz-option nzValue="distance" nzLabel="Distance from main sequence"/>
                    </nz-select>
                </div>

                @if (store.snapshotLoading()) {
                    <div class="centre">
                        <nz-spin nzSimple/>
                    </div>
                } @else {
                    <ul>
                        @for (item of railItems(); track item.pkg.id) {
                            <li>
                                <a
                                        [routerLink]="['/projects', projectId(), 'packages', item.pkg.id]"
                                        routerLinkActive="active"
                                        [title]="item.pkg.name ?? ''"
                                >
                                    <span class="bar" [style.width.%]="item.fraction * 100"
                                          [style.background]="item.color"></span>
                                    <span class="label truncate-start">{{ shortPackageName(item.pkg.name) }}</span>
                                    <span class="value numeric">{{ item.display }}</span>
                                </a>
                            </li>
                        } @empty {
                            <li class="no-match meta">No package matches “{{ filter() }}”.</li>
                        }
                    </ul>
                }
            </nav>

            <section class="content">
                @if (store.snapshotError(); as error) {
                    <nz-alert
                            nzType="error"
                            nzMessage="Couldn't load this project"
                            [nzDescription]="error + ' Check the id in the address bar, or pick another project.'"
                    />
                } @else {
                    <router-outlet/>
                }
            </section>
        </div>
    `,
    styles: [
        `
            :host {
                display: block;
                min-height: 100vh;
            }

            .topbar {
                display: flex;
                align-items: center;
                gap: 10px;
                height: var(--bar-height);
                padding: 0 20px;
                background: var(--surface);
                border-bottom: 1px solid var(--line);
                position: sticky;
                top: 0;
                z-index: 10;
            }

            .home {
                font-weight: 600;
                color: var(--ink);
            }

            .home:hover {
                color: var(--accent);
                text-decoration: none;
            }

            .sep {
                color: var(--ink-3);
            }

            .project {
                font-size: 14px;
                font-weight: 500;
            }

            .right {
                margin-left: auto;
            }

            .frame {
                display: grid;
                grid-template-columns: var(--rail-width) minmax(0, 1fr);
                align-items: start;
            }

            .rail {
                position: sticky;
                top: var(--bar-height);
                height: calc(100vh - var(--bar-height));
                overflow-y: auto;
                background: var(--surface);
                border-right: 1px solid var(--line);
            }

            .controls {
                display: grid;
                gap: 8px;
                padding: 12px;
                border-bottom: 1px solid var(--line-soft);
                position: sticky;
                top: 0;
                background: var(--surface);
            }

            .metric-select {
                width: 100%;
            }

            ul {
                list-style: none;
                margin: 0;
                padding: 4px 0;
            }

            .rail a {
                position: relative;
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 5px 12px;
                color: var(--ink);
                overflow: hidden;
            }

            .rail a:hover {
                text-decoration: none;
                background: var(--canvas);
            }

            .rail a.active {
                background: var(--accent-bg);
                box-shadow: inset 2px 0 0 var(--accent);
            }

            .bar {
                position: absolute;
                left: 0;
                bottom: 0;
                height: 2px;
                opacity: 0.85;
            }

            .label {
                flex: 1;
                min-width: 0;
                font-family: var(--font-mono);
                font-size: 12px;
            }

            .value {
                font-size: 12px;
                color: var(--ink-2);
                flex: none;
            }

            .no-match {
                padding: 12px;
            }

            .content {
                padding: 20px;
                min-width: 0;
            }

            .centre {
                display: flex;
                justify-content: center;
                padding: 32px;
            }
        `,
    ],
})
export class ProjectShellComponent {
    readonly projectId = input.required<string>();

    protected readonly store = inject(AnalysisStore);
    protected readonly filter = signal('');
    protected readonly metric = signal<RailMetric>('complexity');

    protected readonly shortPackageName = shortPackageName;
    protected readonly formatDateTime = formatDateTime;
    protected readonly humanise = humanise;
    protected readonly analysisStatusConcern = analysisStatusConcern;

    protected readonly inProgress = computed(() =>
        isAnalysisInProgress(this.store.project()?.analysisStatus ?? null),
    );

    protected readonly railItems = computed<RailItem[]>(() => {
        const needle = this.filter().trim().toLowerCase();
        const metric = this.metric();
        const packages = this.store
            .packages()
            .filter((p) => !needle || (p.name ?? '').toLowerCase().includes(needle));

        const value = (p: PackageSummary): number => {
            switch (metric) {
                case 'linesOfCode':
                    return num(p.linesOfCode);
                case 'defectDensity':
                    return num(p.defectDensity);
                case 'findings':
                    return num(p.totalNumberOfFindings);
                case 'distance':
                    return num(p.componentDependency?.distance);
                default:
                    return num(p.complexity);
            }
        };

        const display = (v: number): string =>
            metric === 'defectDensity' || metric === 'distance' ? decimal(v) : count(v);

        const highest = Math.max(1, ...packages.map(value));

        return packages
            .map((pkg) => ({
                pkg,
                value: value(pkg),
                display: display(value(pkg)),
                fraction: value(pkg) / highest,
                color: concernColor(packageComplexityConcern(pkg.complexityRating)),
            }))
            .sort((a, b) => b.value - a.value);
    });

    constructor() {
        // The input is the only thing this effect should track. Wrapping the call
        // in untracked() means no signal the store touches can retrigger it.
        effect(() => {
            const id = this.projectId();
            untracked(() => this.store.loadProject(id));
        });
    }
}
