import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { formatDateTime } from '../shared/format';
import { analysisStatusConcern, humanise, isAnalysisInProgress } from '../shared/ratings';
import { EmptyComponent, RatingComponent } from '../shared/ui.components';
import { AnalysisStore } from '../state/analysis.store';

/**
 * The landing view, and the only place in the app that writes.
 *
 * A workspace is a directory the backend scans for projects, so the three
 * actions here are a chain rather than a menu: create one, let it locate its
 * projects, then analyse them. The layout follows that order, and each step
 * only appears once the previous one has produced something.
 */
@Component({
  selector: 'app-workspace-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    FormsModule,
    RouterLink,
    NzAlertModule,
    NzButtonModule,
    NzInputModule,
    NzSpinModule,
    RatingComponent,
    EmptyComponent,
  ],
  template: `
    <main>
      <header class="page">
        <h1>Workspaces</h1>
        <button nz-button nzType="primary" (click)="toggleForm()">
          {{ showForm() ? 'Cancel' : 'New workspace' }}
        </button>
      </header>
      <p class="muted intro">
        A workspace is a directory on the analysis host. Locate its projects, then analyse them.
      </p>

      @if (store.actionError(); as error) {
        <nz-alert
          nzType="error"
          nzMessage="That didn't work"
          [nzDescription]="error"
          nzCloseable
          (nzOnClose)="store.dismissActionError()"
        />
      }

      @if (showForm()) {
        <section class="form">
          <label>
            <span>Name</span>
            <input nz-input [ngModel]="name()" (ngModelChange)="name.set($event)" placeholder="Backend services" />
          </label>
          <label>
            <span>Path</span>
            <input
              nz-input
              class="mono"
              [ngModel]="path()"
              (ngModelChange)="path.set($event)"
              placeholder="/home/me/src/backend"
            />
          </label>
          <label>
            <span>Base package</span>
            <input
              nz-input
              class="mono"
              [ngModel]="basePackage()"
              (ngModelChange)="basePackage.set($event)"
              placeholder="org.example"
            />
          </label>
          <label>
            <span>Excluded paths</span>
            <textarea
              nz-input
              rows="3"
              class="mono"
              [ngModel]="excludedPaths()"
              (ngModelChange)="excludedPaths.set($event)"
              placeholder="One path per line, e.g.&#10;target&#10;build&#10;.git"
            ></textarea>
          </label>
          <div class="actions">
            <span class="meta">{{ formHint() }}</span>
            <button
              nz-button
              nzType="primary"
              [disabled]="!canSubmit()"
              [nzLoading]="store.busy().has('create-workspace')"
              (click)="submit()"
            >
              Create workspace
            </button>
          </div>
        </section>
      }

      @if (store.workspacesError(); as error) {
        <nz-alert
          nzType="warning"
          nzMessage="The workspaces query isn't available"
          [nzDescription]="
            error +
            ' Showing all projects instead. Adjust the WORKSPACES document in src/app/api/queries.ts to match your schema.'
          "
        />

        <h2 class="fallback-heading">Projects</h2>
        @if (store.projectsLoading()) {
          <div class="centre"><nz-spin nzSimple /></div>
        } @else {
          <ul class="projects standalone">
            @for (project of store.projects(); track project.id) {
              <li>
                <a class="identifier grow" [routerLink]="['/projects', project.id]">
                  {{ project.name ?? 'unnamed project' }}
                </a>
                <app-rating
                  [label]="humanise(project.analysisStatus)"
                  [concern]="analysisStatusConcern(project.analysisStatus)"
                />
                <span class="meta">{{ formatDateTime(project.lastAnalyzed) }}</span>
                <button
                  nz-button
                  nzSize="small"
                  [disabled]="isAnalysisInProgress(project.analysisStatus)"
                  [nzLoading]="store.busy().has('analysis:' + project.id)"
                  (click)="store.scheduleAnalysis(project.id)"
                >
                  Analyse
                </button>
              </li>
            } @empty {
              <app-empty headline="No projects yet" />
            }
          </ul>
        }
      } @else if (store.workspacesLoading() && store.workspaces().length === 0) {
        <div class="centre"><nz-spin nzSimple /></div>
      } @else if (store.workspaces().length === 0) {
        <app-empty
          headline="No workspaces yet"
          detail="Create one above and point it at a directory containing Java projects."
        />
      } @else {
        @for (workspace of store.workspaces(); track workspace.id) {
          <section class="workspace">
            <header>
              <h2>{{ workspace.name ?? 'unnamed workspace' }}</h2>
              <span class="identifier meta path">{{ workspace.path }}</span>
              <button
                nz-button
                nzSize="small"
                [nzLoading]="store.busy().has('locate:' + workspace.id)"
                (click)="store.locateProjects(workspace.id)"
              >
                Locate projects
              </button>
            </header>

            <div class="config">
              @if (workspace.basePackage) {
                <span class="chip identifier">{{ workspace.basePackage }}</span>
              }
              @for (excluded of workspace.excludedPaths; track excluded) {
                <span class="chip excluded identifier">{{ excluded }}</span>
              }
            </div>

            <ul class="projects">
              @for (project of workspace.projects; track project.id) {
                <li>
                  <a class="identifier grow" [routerLink]="['/projects', project.id]">
                    {{ project.name ?? 'unnamed project' }}
                  </a>
                  <app-rating
                    [label]="humanise(project.analysisStatus)"
                    [concern]="analysisStatusConcern(project.analysisStatus)"
                  />
                  <span class="meta">{{ formatDateTime(project.lastAnalyzed) }}</span>
                  <button
                    nz-button
                    nzSize="small"
                    [disabled]="isAnalysisInProgress(project.analysisStatus)"
                    [nzLoading]="store.busy().has('analysis:' + project.id)"
                    (click)="store.scheduleAnalysis(project.id)"
                  >
                    {{ isAnalysisInProgress(project.analysisStatus) ? 'Running' : 'Analyse' }}
                  </button>
                </li>
              } @empty {
                <li class="none meta">
                  No projects located yet. Use “Locate projects” to scan the directory.
                </li>
              }
            </ul>
          </section>
        }
      }
    </main>
  `,
  styles: [
    `
      main {
        max-width: 860px;
        margin: 0 auto;
        padding: 40px 24px 64px;
        display: grid;
        gap: 16px;
      }
      header.page {
        display: flex;
        align-items: center;
        gap: 16px;
      }
      header.page h1 {
        flex: 1;
      }
      .intro {
        margin: -12px 0 0;
      }
      .form {
        display: grid;
        gap: 12px;
        background: var(--surface);
        border: 1px solid var(--line);
        border-radius: var(--radius);
        padding: 16px;
      }
      .form label {
        display: grid;
        grid-template-columns: 120px minmax(0, 1fr);
        gap: 12px;
        align-items: start;
      }
      .form label > span {
        color: var(--ink-2);
        font-size: 13px;
        padding-top: 6px;
      }
      .mono {
        font-family: var(--font-mono);
        font-size: 13px;
      }
      .actions {
        display: flex;
        align-items: center;
        gap: 12px;
        justify-content: flex-end;
      }
      .actions .meta {
        margin-right: auto;
      }
      .workspace {
        background: var(--surface);
        border: 1px solid var(--line);
        border-radius: var(--radius);
        overflow: hidden;
      }
      .workspace > header {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 16px;
      }
      .workspace > header h2 {
        flex: none;
      }
      .path {
        flex: 1;
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      .config {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        padding: 0 16px 12px;
      }
      .chip {
        font-size: 12px;
        padding: 1px 8px;
        border-radius: 10px;
        background: var(--accent-bg);
        color: var(--accent);
      }
      .chip.excluded {
        background: var(--rate-none-bg);
        color: var(--ink-2);
        text-decoration: line-through;
      }
      .projects {
        list-style: none;
        margin: 0;
        padding: 0;
        border-top: 1px solid var(--line-soft);
      }
      .projects.standalone {
        border: 1px solid var(--line);
        border-radius: var(--radius);
        background: var(--surface);
      }
      .projects li {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px 16px;
      }
      .projects li + li {
        border-top: 1px solid var(--line-soft);
      }
      .grow {
        flex: 1;
        min-width: 0;
        color: var(--ink);
        font-weight: 500;
      }
      .grow:hover {
        color: var(--accent);
      }
      .none {
        display: block;
      }
      .fallback-heading {
        margin-top: 8px;
      }
      .centre {
        display: flex;
        justify-content: center;
        padding: 48px;
      }
    `,
  ],
})
export class WorkspaceListComponent {
  protected readonly store = inject(AnalysisStore);

  protected readonly showForm = signal(false);
  protected readonly name = signal('');
  protected readonly path = signal('');
  protected readonly basePackage = signal('');
  protected readonly excludedPaths = signal('');

  protected readonly formatDateTime = formatDateTime;
  protected readonly humanise = humanise;
  protected readonly analysisStatusConcern = analysisStatusConcern;
  protected readonly isAnalysisInProgress = isAnalysisInProgress;

  protected readonly canSubmit = computed(
    () => this.name().trim().length > 0 && this.path().trim().length > 0,
  );

  protected readonly formHint = computed(() =>
    this.canSubmit() ? '' : 'Name and path are required.',
  );

  constructor() {
    this.store.loadWorkspaces();
    // Loaded eagerly so the fallback list is ready if the workspaces query is
    // not available on this backend.
    this.store.loadProjects();
    // An analysis may already have been running when the page was opened.
    this.store.startPolling();
  }

  protected toggleForm(): void {
    this.showForm.update((open) => !open);
  }

  protected submit(): void {
    if (!this.canSubmit()) return;
    this.store.createWorkspace(
      {
        name: this.name().trim(),
        path: this.path().trim(),
        basePackage: this.basePackage().trim(),
        excludedPaths: this.excludedPaths()
          .split(/[\n,]/)
          .map((value) => value.trim())
          .filter((value) => value.length > 0),
      },
      () => {
        this.name.set('');
        this.path.set('');
        this.basePackage.set('');
        this.excludedPaths.set('');
        this.showForm.set(false);
      },
    );
  }
}
