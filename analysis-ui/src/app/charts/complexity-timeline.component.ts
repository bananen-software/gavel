import {
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    effect,
    ElementRef,
    inject,
    input,
    viewChild,
} from '@angular/core';
import {Chart} from 'chart.js';
import type {ClassContribution} from '../api/schema.types';
import {formatDate, num, parseDate} from '../shared/format';
import {resolveColor} from './chart-defaults';

/**
 * Complexity as it accumulated, commit by commit.
 *
 * The only genuinely temporal data in the schema, and the most persuasive
 * artefact in the app: a line that only ever goes up is an argument no static
 * metric can make. Added complexity per commit sits underneath on its own axis
 * so you can see which commits did the damage.
 */
@Component({
    selector: 'app-complexity-timeline',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `
        <canvas #canvas></canvas>`,
    styles: [
        `
            :host {
                display: block;
                position: relative;
                height: 300px;
            }
        `,
    ],
})
export class ComplexityTimelineComponent {
    readonly contributions = input.required<ClassContribution[]>();

    private readonly canvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas');
    private chart: Chart | null = null;

    constructor() {
        inject(DestroyRef).onDestroy(() => this.chart?.destroy());
        effect(() => this.render(this.contributions()));
    }

    private render(contributions: ClassContribution[]): void {
        const ordered = [...contributions]
            .filter((c) => parseDate(c.timestamp) !== null)
            .sort(
                (a, b) =>
                    (parseDate(a.timestamp)?.getTime() ?? 0) - (parseDate(b.timestamp)?.getTime() ?? 0),
            );

        this.chart?.destroy();

        this.chart = new Chart(this.canvasRef().nativeElement, {
            type: 'line',
            data: {
                labels: ordered.map((c) => formatDate(c.timestamp)),
                datasets: [
                    {
                        type: 'line',
                        label: 'Complexity',
                        data: ordered.map((c) => num(c.complexity?.complexity)),
                        borderColor: resolveColor('var(--accent)'),
                        backgroundColor: resolveColor('var(--accent-bg)'),
                        borderWidth: 2,
                        pointRadius: 0,
                        pointHitRadius: 12,
                        fill: false,
                        stepped: 'after',
                        yAxisID: 'y',
                    },
                    {
                        type: 'bar',
                        label: 'Added',
                        data: ordered.map((c) => num(c.complexity?.addedComplexity)),
                        backgroundColor: resolveColor('var(--rate-high)'),
                        yAxisID: 'yAdded',
                    },
                ],
            },
            options: {
                interaction: {mode: 'index', intersect: false},
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom',
                        labels: {boxWidth: 10, boxHeight: 10, padding: 12},
                    },
                    tooltip: {
                        displayColors: true,
                        callbacks: {
                            afterTitle: (items) => {
                                const contribution = ordered[items[0].dataIndex];
                                const author = contribution.author?.name ?? 'unknown author';
                                const revision = contribution.vcsIdentifier?.slice(0, 8) ?? '';
                                return `${author}${revision ? ` · ${revision}` : ''}`;
                            },
                        },
                    },
                },
                scales: {
                    x: {grid: {display: false}, ticks: {maxTicksLimit: 8, font: {size: 10}}},
                    y: {beginAtZero: true, position: 'left'},
                    yAdded: {
                        beginAtZero: true,
                        position: 'right',
                        grid: {display: false},
                        title: {display: true, text: 'added', color: resolveColor('var(--ink-3)')},
                    },
                },
            },
        });
    }
}
