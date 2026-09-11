import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  effect,
  inject,
  input,
  viewChild,
} from '@angular/core';
import { Chart } from 'chart.js';
import type { PackageSummary } from '../api/schema.types';
import { num, shortPackageName } from '../shared/format';
import { resolveColor } from './chart-defaults';

/**
 * How complexity distributes across each package's types. Sorted by very-high
 * count so the packages worth opening are at the top, and capped at a readable
 * number of rows — the full list lives in the table below it.
 */
@Component({
  selector: 'app-complexity-mix',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<canvas #canvas></canvas>`,
  styles: [
    `
      :host {
        display: block;
        position: relative;
        height: 320px;
      }
    `,
  ],
})
export class ComplexityMixComponent {
  readonly packages = input.required<PackageSummary[]>();
  readonly limit = input(10);

  private readonly canvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas');
  private chart: Chart | null = null;

  constructor() {
    inject(DestroyRef).onDestroy(() => this.chart?.destroy());
    effect(() => this.render(this.packages(), this.limit()));
  }

  private render(packages: PackageSummary[], limit: number): void {
    const rows = [...packages]
      .sort(
        (a, b) =>
          num(b.numberOfVeryHighComplexityTypes) + num(b.numberOfHighComplexityTypes) -
          (num(a.numberOfVeryHighComplexityTypes) + num(a.numberOfHighComplexityTypes)),
      )
      .slice(0, limit);

    this.chart?.destroy();

    this.chart = new Chart(this.canvasRef().nativeElement, {
      type: 'bar',
      data: {
        labels: rows.map((p) => shortPackageName(p.name)),
        datasets: [
          {
            label: 'Low',
            data: rows.map((p) => num(p.numberOfLowComplexityTypes)),
            backgroundColor: resolveColor('var(--rate-low)'),
          },
          {
            label: 'Medium',
            data: rows.map((p) => num(p.numberOfMediumComplexityTypes)),
            backgroundColor: resolveColor('var(--rate-medium)'),
          },
          {
            label: 'High',
            data: rows.map((p) => num(p.numberOfHighComplexityTypes)),
            backgroundColor: resolveColor('var(--rate-high)'),
          },
          {
            label: 'Very high',
            data: rows.map((p) => num(p.numberOfVeryHighComplexityTypes)),
            backgroundColor: resolveColor('var(--rate-very-high)'),
          },
        ],
      },
      options: {
        indexAxis: 'y',
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: { boxWidth: 10, boxHeight: 10, padding: 12 },
          },
          tooltip: { displayColors: true },
        },
        scales: {
          x: { stacked: true, beginAtZero: true, grid: { display: true } },
          y: { stacked: true, grid: { display: false }, ticks: { font: { size: 10 } } },
        },
      },
    });
  }
}
