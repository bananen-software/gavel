import { ChangeDetectionStrategy, Component, computed, effect, inject, input, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzTableModule } from 'ng-zorro-antd/table';
import { ComplexityTimelineComponent } from '../charts/complexity-timeline.component';
import type { Finding, Severity } from '../api/schema.types';
import { count, decimal, formatDate, formatDateTime, num, simpleClassName } from '../shared/format';
import {
  classComplexityConcern,
  humanise,
  severityConcern,
  stratumColor,
} from '../shared/ratings';
import {
  EmptyComponent,
  MetricComponent,
  PanelComponent,
  RatingComponent,
} from '../shared/ui.components';
import { AnalysisStore } from '../state/analysis.store';

interface ContributorRow {
  name: string;
  email: string;
  commits: number;
  addedComplexity: number;
  share: number;
}

const SEVERITY_RANK: Record<Severity, number> = { HIGH: 0, MEDIUM: 1, LOW: 2 };

@Component({
  selector: 'app-class-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterLink,
    NzAlertModule,
    NzSpinModule,
    NzTableModule,
    ComplexityTimelineComponent,
    MetricComponent,
    PanelComponent,
    RatingComponent,
    EmptyComponent,
  ],
  template: `
    @if (state().error; as error) {
      <nz-alert nzType="error" nzMessage="Couldn't load this class" [nzDescription]="error" />
    } @else if (state().loading) {
      <div class="centre"><nz-spin nzSimple /></div>
    } @else if (state().value; as cls) {
      <header class="crumb">
        <a [routerLink]="['/projects', projectId()]">All packages</a>
        <span class="sep">›</span>
        <a [routerLink]="['/projects', projectId(), 'packages', packageId()]">Package</a>
        <span class="sep">›</span>
        <span class="identifier">{{ simpleClassName(cls.name) }}</span>
        <app-rating
          [label]="humanise(cls.complexityRating)"
          [concern]="classComplexityConcern(cls.complexityRating)"
        />
        <span class="stratum" [style.color]="stratumColor(cls.stratum)">
          {{ humanise(cls.stratum, 'None') }} layer
        </span>
        <span class="meta right">last change {{ formatDateTime(cls.lastModified) }}</span>
      </header>

      <div class="metrics">
        <app-metric
          label="Complexity"
          [value]="count(cls.complexity)"
          [hint]="count(cls.numberOfResponsibilities) + ' responsibilities'"
        />
        <app-metric
          label="Lines of code"
          [value]="count(cls.totalLinesOfCode)"
          [hint]="'comment ratio ' + decimal(cls.commentToCodeRatio)"
        />
        <app-metric
          label="Changes"
          [value]="count(cls.numberOfChanges)"
          [hint]="count(cls.numberOfAuthors) + ' authors'"
        />
        <app-metric
          label="Findings"
          [value]="count(cls.totalNumberOfFindings)"
          [hint]="count(cls.numberOfHighPriorityFindings) + ' high priority'"
        />
      </div>

      <app-panel heading="How complexity accumulated" note="one point per recorded contribution">
        @if (cls.contributions.length) {
          <app-complexity-timeline [contributions]="cls.contributions" />
        } @else {
          <app-empty
            headline="No version history for this class"
            detail="The analyser found no contributions in the repository."
          />
        }
      </app-panel>

      <div class="section-grid">
        <app-panel heading="Contributors" [note]="count(contributors().length) + ' people'" [flush]="true">
          @if (contributors().length) {
            <ul class="contributors">
              @for (person of contributors(); track person.email) {
                <li>
                  <div class="who">
                    <span class="name">{{ person.name }}</span>
                    <span class="meta">{{ person.email }}</span>
                  </div>
                  <div class="bars">
                    <span class="track">
                      <span class="fill" [style.width.%]="person.share * 100"></span>
                    </span>
                    <span class="numbers numeric">
                      {{ count(person.commits) }} commits
                      <span class="added">+{{ count(person.addedComplexity) }}</span>
                    </span>
                  </div>
                </li>
              }
            </ul>
          } @else {
            <app-empty headline="No recorded contributors" />
          }
        </app-panel>

        <app-panel heading="Findings" [note]="count(cls.findings.length) + ' total'" [flush]="true">
          @if (cls.findings.length) {
            <nz-table
              #findingsTable
              [nzData]="sortedFindings()"
              nzSize="small"
              [nzPageSize]="12"
              [nzShowPagination]="cls.findings.length > 12"
            >
              <thead>
                <tr>
                  <th nzWidth="90px">Severity</th>
                  <th>Rule</th>
                  <th nzWidth="90px">Tool</th>
                </tr>
              </thead>
              <tbody>
                @for (finding of findingsTable.data; track finding.id ?? finding.description) {
                  <tr class="finding">
                    <td>
                      <app-rating
                        [label]="humanise(finding.severity)"
                        [concern]="severityConcern(finding.severity)"
                      />
                    </td>
                    <td>
                      <div class="identifier">{{ finding.ruleName ?? 'unnamed rule' }}</div>
                      <div class="meta">{{ finding.description }}</div>
                    </td>
                    <td class="meta">{{ finding.tool }}</td>
                  </tr>
                }
              </tbody>
            </nz-table>
          } @else {
            <app-empty headline="No findings on this class" detail="Nothing to fix here." />
          }
        </app-panel>
      </div>
    } @else {
      <app-empty headline="Class not found" detail="Go back to the package and pick another." />
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
        flex-wrap: wrap;
      }
      .crumb .identifier {
        font-size: 14px;
        font-weight: 500;
      }
      .sep {
        color: var(--ink-3);
      }
      .right {
        margin-left: auto;
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
      .contributors {
        list-style: none;
        margin: 0;
        padding: 0;
      }
      .contributors li {
        display: grid;
        gap: 4px;
        padding: 10px 14px;
      }
      .contributors li + li {
        border-top: 1px solid var(--line-soft);
      }
      .who {
        display: flex;
        align-items: baseline;
        gap: 8px;
      }
      .name {
        font-weight: 500;
        font-size: 13px;
      }
      .bars {
        display: flex;
        align-items: center;
        gap: 10px;
      }
      .track {
        flex: 1;
        height: 4px;
        background: var(--line-soft);
        border-radius: 2px;
        overflow: hidden;
      }
      .fill {
        display: block;
        height: 100%;
        background: var(--accent);
      }
      .numbers {
        font-size: 12px;
        color: var(--ink-2);
        flex: none;
      }
      .added {
        color: var(--rate-high);
        margin-left: 6px;
      }
      .finding td {
        vertical-align: top;
      }
      .centre {
        display: flex;
        justify-content: center;
        padding: 48px;
      }
    `,
  ],
})
export class ClassDetailComponent {
  readonly projectId = input.required<string>();
  readonly packageId = input.required<string>();
  readonly classId = input.required<string>();

  protected readonly store = inject(AnalysisStore);

  protected readonly state = computed(() => this.store.classDetail(this.classId())());

  protected readonly count = count;
  protected readonly decimal = decimal;
  protected readonly formatDate = formatDate;
  protected readonly formatDateTime = formatDateTime;
  protected readonly humanise = humanise;
  protected readonly simpleClassName = simpleClassName;
  protected readonly classComplexityConcern = classComplexityConcern;
  protected readonly severityConcern = severityConcern;
  protected readonly stratumColor = stratumColor;

  /**
   * Grouped on email rather than the author id: an id is now in the schema, but
   * the same human commits under several ids in most repositories, and email is
   * what actually identifies them across those.
   */
  protected readonly contributors = computed<ContributorRow[]>(() => {
    const contributions = this.state().value?.contributions ?? [];
    const byEmail = new Map<string, ContributorRow>();

    for (const contribution of contributions) {
      const email = contribution.author?.email ?? 'unknown';
      const row = byEmail.get(email) ?? {
        name: contribution.author?.name ?? 'Unknown author',
        email,
        commits: 0,
        addedComplexity: 0,
        share: 0,
      };
      row.commits += 1;
      row.addedComplexity += num(contribution.complexity?.addedComplexity);
      byEmail.set(email, row);
    }

    const rows = [...byEmail.values()].sort((a, b) => b.commits - a.commits);
    const busiest = Math.max(1, ...rows.map((r) => r.commits));
    return rows.map((row) => ({ ...row, share: row.commits / busiest }));
  });

  /** High severity first: the list is long and the top of it is what gets read. */
  protected readonly sortedFindings = computed<Finding[]>(() =>
    [...(this.state().value?.findings ?? [])].sort(
      (a, b) =>
        (a.severity ? SEVERITY_RANK[a.severity] : 3) - (b.severity ? SEVERITY_RANK[b.severity] : 3),
    ),
  );

  constructor() {
    effect(() => {
      const id = this.classId();
      untracked(() => this.store.loadClassDetail(id));
    });
  }
}
