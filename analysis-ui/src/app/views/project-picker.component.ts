import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {RouterLink} from '@angular/router';
import {NzAlertModule} from 'ng-zorro-antd/alert';
import {NzSpinModule} from 'ng-zorro-antd/spin';
import {AnalysisStore} from '../state/analysis.store';
import {formatDateTime} from '../shared/format';
import {analysisStatusConcern, humanise} from '../shared/ratings';
import {EmptyComponent, RatingComponent} from '../shared/ui.components';

@Component({
    selector: 'app-project-picker',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [RouterLink, NzAlertModule, NzSpinModule, RatingComponent, EmptyComponent],
    template: `
        <main>
            <h1>Projects</h1>
            <p class="muted intro">Pick a project to explore its packages, classes and findings.</p>

            @if (store.projectsError(); as error) {
                <nz-alert nzType="error" nzMessage="Couldn't load projects" [nzDescription]="error"/>
            }

            @if (store.projectsLoading()) {
                <div class="centre">
                    <nz-spin nzSimple/>
                </div>
            } @else if (store.projects().length === 0 && !store.projectsError()) {
                <app-empty
                        headline="No projects yet"
                        detail="Run an analysis on the backend and it will appear here."
                />
            } @else {
                <ul>
                    @for (project of store.projects(); track project.id) {
                        <li>
                            <a [routerLink]="['/projects', project.id]">
                                <span class="name identifier">{{ project.name ?? 'unnamed project' }}</span>
                                <app-rating
                                        [label]="humanise(project.analysisStatus)"
                                        [concern]="analysisStatusConcern(project.analysisStatus)"
                                />
                                <span class="meta">analysed {{ formatDateTime(project.lastAnalyzed) }}</span>
                            </a>
                        </li>
                    }
                </ul>
            }
        </main>
    `,
    styles: [
        `
            main {
                max-width: 720px;
                margin: 0 auto;
                padding: 48px 24px;
            }

            .intro {
                margin: 4px 0 24px;
            }

            ul {
                list-style: none;
                margin: 0;
                padding: 0;
                background: var(--surface);
                border: 1px solid var(--line);
                border-radius: var(--radius);
            }

            li + li {
                border-top: 1px solid var(--line-soft);
            }

            a {
                display: flex;
                align-items: center;
                gap: 12px;
                padding: 12px 16px;
                color: var(--ink);
            }

            a:hover {
                background: var(--accent-bg);
                text-decoration: none;
            }

            .name {
                font-size: 14px;
                font-weight: 500;
            }

            .meta {
                margin-left: auto;
            }

            .centre {
                display: flex;
                justify-content: center;
                padding: 48px;
            }
        `,
    ],
})
export class ProjectPickerComponent {
    protected readonly store = inject(AnalysisStore);
    protected readonly formatDateTime = formatDateTime;
    protected readonly humanise = humanise;
    protected readonly analysisStatusConcern = analysisStatusConcern;

    constructor() {
        this.store.loadProjects();
    }
}
