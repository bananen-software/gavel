import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  effect,
  input,
  output,
  viewChild,
} from '@angular/core';
import { max } from 'd3-array';
import { axisBottom, axisLeft } from 'd3-axis';
import { scaleLinear, scaleSqrt } from 'd3-scale';
import { select } from 'd3-selection';
import type { PackageSummary } from '../api/schema.types';
import { decimal, num, shortPackageName } from '../shared/format';

/**
 * Abstractness against instability, with the main sequence (A + I = 1) drawn in.
 *
 * This is the single most informative chart the schema supports. Packages far
 * below the line are concrete and heavily depended on — hard to change, easy to
 * break. Packages far above it are abstract and unused. `distance` is already
 * computed server-side, so it drives the colour directly.
 */
@Component({
  selector: 'app-main-sequence',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<svg
    #svg
    role="img"
    aria-label="Package abstractness plotted against instability"
  ></svg>`,
  styles: [
    `
      :host {
        display: block;
      }
      svg {
        width: 100%;
        height: 320px;
        display: block;
      }
      svg ::ng-deep .domain,
      svg ::ng-deep .tick line {
        stroke: var(--line);
      }
      svg ::ng-deep .tick text {
        fill: var(--ink-3);
        font-size: 10px;
      }
      svg ::ng-deep circle {
        cursor: pointer;
      }
    `,
  ],
})
export class MainSequenceComponent {
  readonly packages = input.required<PackageSummary[]>();
  readonly select = output<PackageSummary>();

  private readonly svgRef = viewChild.required<ElementRef<SVGSVGElement>>('svg');

  constructor() {
    effect(() => this.render(this.packages()));
  }

  private render(packages: PackageSummary[]): void {
    const width = 560;
    const height = 320;
    const margin = { top: 18, right: 18, bottom: 40, left: 46 };

    const svg = select(this.svgRef().nativeElement)
      .attr('viewBox', `0 0 ${width} ${height}`)
      .attr('preserveAspectRatio', 'xMidYMid meet');
    svg.selectAll('*').remove();

    const plotted = packages.filter((p) => p.componentDependency != null);
    if (plotted.length === 0) {
      svg
        .append('text')
        .attr('x', width / 2)
        .attr('y', height / 2)
        .attr('text-anchor', 'middle')
        .attr('fill', 'var(--ink-3)')
        .attr('font-size', 13)
        .text('No coupling data for this project');
      return;
    }

    const x = scaleLinear().domain([0, 1]).range([margin.left, width - margin.right]);
    const y = scaleLinear().domain([0, 1]).range([height - margin.bottom, margin.top]);

    // The two zones the plot exists to reveal, labelled so the chart teaches itself.
    svg
      .append('text')
      .attr('x', x(0.02))
      .attr('y', y(0.06))
      .attr('fill', 'var(--ink-3)')
      .attr('font-size', 10)
      .text('zone of pain');
    svg
      .append('text')
      .attr('x', x(0.98))
      .attr('y', y(0.95))
      .attr('text-anchor', 'end')
      .attr('fill', 'var(--ink-3)')
      .attr('font-size', 10)
      .text('zone of uselessness');

    svg
      .append('line')
      .attr('x1', x(0))
      .attr('y1', y(1))
      .attr('x2', x(1))
      .attr('y2', y(0))
      .attr('stroke', 'var(--ink-3)')
      .attr('stroke-dasharray', '4 4');

    svg
      .append('g')
      .attr('transform', `translate(0,${height - margin.bottom})`)
      .call(axisBottom(x).ticks(5).tickSizeOuter(0));

    svg
      .append('g')
      .attr('transform', `translate(${margin.left},0)`)
      .call(axisLeft(y).ticks(5).tickSizeOuter(0));

    svg
      .append('text')
      .attr('x', width - margin.right)
      .attr('y', height - 6)
      .attr('text-anchor', 'end')
      .attr('fill', 'var(--ink-2)')
      .attr('font-size', 11)
      .text('instability →');

    svg
      .append('text')
      .attr('transform', `translate(12,${margin.top + 4}) rotate(-90)`)
      .attr('text-anchor', 'end')
      .attr('fill', 'var(--ink-2)')
      .attr('font-size', 11)
      .text('← abstractness');

    // scaleSqrt so circle *area* tracks type count; a linear radius scale would
    // exaggerate large packages by the square.
    const radius = scaleSqrt()
      .domain([0, max(plotted, (p) => num(p.numberOfTypes)) ?? 1])
      .range([3, 15]);

    svg
      .append('g')
      .selectAll('circle')
      .data(plotted)
      .join('circle')
      .attr('cx', (p) => x(num(p.componentDependency?.instability)))
      .attr('cy', (p) => y(num(p.componentDependency?.abstractness)))
      .attr('r', (p) => radius(num(p.numberOfTypes)))
      .attr('fill', (p) => distanceColor(num(p.componentDependency?.distance)))
      .attr('fill-opacity', 0.72)
      .attr('stroke', 'var(--surface)')
      .on('click', (_event, p) => this.select.emit(p))
      .append('title')
      .text(
        (p) =>
          `${shortPackageName(p.name)}\n` +
          `instability ${decimal(p.componentDependency?.instability)}, ` +
          `abstractness ${decimal(p.componentDependency?.abstractness)}\n` +
          `distance ${decimal(p.componentDependency?.distance)}, ` +
          `${num(p.numberOfTypes)} types`,
      );
  }
}

const distanceColor = (distance: number): string => {
  if (distance < 0.1) return 'var(--rate-low)';
  if (distance < 0.3) return 'var(--rate-medium)';
  if (distance < 0.5) return 'var(--rate-high)';
  return 'var(--rate-very-high)';
};
