import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  effect,
  inject,
  input,
  output,
  viewChild,
} from '@angular/core';
import { Chart } from 'chart.js';
import type { ClassSummary } from '../api/schema.types';
import { num, simpleClassName } from '../shared/format';
import { classComplexityConcern, concernColor } from '../shared/ratings';
import { resolveColor } from './chart-defaults';

interface HotspotPoint {
  x: number;
  y: number;
  r: number;
  label: string;
  id: string;
}

/**
 * Churn against complexity, sized by lines of code.
 *
 * Complexity alone tells you what is hard to read. Complexity crossed with how
 * often a file actually changes tells you what is expensive — the top right is
 * where refactoring pays for itself. This is the chart that changes what
 * somebody does on Monday morning.
 */
@Component({
  selector: 'app-hotspot-chart',
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
export class HotspotChartComponent {
  readonly classes = input.required<ClassSummary[]>();
  readonly select = output<string>();

  private readonly canvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas');
  private chart: Chart<'bubble', HotspotPoint[]> | null = null;

  constructor() {
    inject(DestroyRef).onDestroy(() => this.chart?.destroy());
    effect(() => this.render(this.classes()));
  }

  private render(classes: ClassSummary[]): void {
    const largest = Math.max(1, ...classes.map((c) => num(c.totalLinesOfCode)));
    const points: HotspotPoint[] = classes.map((c) => ({
      x: num(c.numberOfChanges),
      y: num(c.complexity),
      // Floor and cap the radius: unbounded sizes let one god class swallow the plot.
      r: 4 + 16 * Math.sqrt(num(c.totalLinesOfCode) / largest),
      label: simpleClassName(c.name),
      id: c.id,
    }));

    const colors = classes.map((c) =>
      resolveColor(concernColor(classComplexityConcern(c.complexityRating))),
    );

    this.chart?.destroy();

    this.chart = new Chart<'bubble', HotspotPoint[]>(this.canvasRef().nativeElement, {
      type: 'bubble',
      data: {
        datasets: [
          {
            data: points,
            backgroundColor: colors.map((c) => `${c}bf`),
            borderColor: colors,
            borderWidth: 1,
          },
        ],
      },
      options: {
        onClick: (_event, elements) => {
          const hit = elements[0];
          if (hit) this.select.emit(points[hit.index].id);
        },
        plugins: {
          tooltip: {
            callbacks: {
              label: (item) => {
                const point = item.raw as HotspotPoint;
                return `${point.label} — ${point.x} changes, complexity ${point.y}`;
              },
            },
          },
        },
        scales: {
          x: {
            title: { display: true, text: 'changes', color: resolveColor('var(--ink-2)') },
            beginAtZero: true,
          },
          y: {
            title: { display: true, text: 'complexity', color: resolveColor('var(--ink-2)') },
            beginAtZero: true,
          },
        },
      },
    });
  }
}
