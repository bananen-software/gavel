import {
  afterRender,
  Component,
  computed,
  ElementRef,
  input,
  viewChild,
} from '@angular/core';
import * as d3 from 'd3';
import { ComponentDependency } from '../component-dependency-metrics/component-dependency-metrics.service';

class Zone {
  constructor(
    public upperBound: number,
    public lowerBound: number,
    public color: string,
    public name: 'Green' | 'Yellow' | 'Red'
  ) {}
}

const green = new Zone(0.3, 0, 'green', 'Green');

const yellow = new Zone(0.6, 0.3, 'yellow', 'Yellow');

const red = new Zone(1, 0.6, 'red', 'Red');

const zones: Zone[] = [red, yellow, green];

type Spec = {
  width: number;
  height: number;
  margin: {
    left: number;
    top: number;
    right: number;
    bottom: number;
  };
};

function createSvg(spec: Spec) {
  const x = d3.scaleLinear().domain([0, 1]).range([0, spec.width]);
  const y = d3.scaleLinear().domain([0, 1]).range([spec.height, 0]);

  const svg = d3
    .create('svg')
    .attr('width', spec.width + spec.margin.left + spec.margin.right)
    .attr('height', spec.height + spec.margin.top + spec.margin.bottom);

  svg
    .append('g')
    .attr(
      'transform',
      'translate(' + spec.margin.left + ',' + spec.margin.top + ')'
    );

  svg
    .append('text')
    .attr('text-anchor', 'end')
    .attr('x', spec.width)
    .attr('y', spec.height + spec.margin.top + 25)
    .style('fill', 'var(--p-card-color)')
    .text('Abstractness');

  svg
    .append('g')
    .attr('transform', 'translate(0,' + spec.height + ')')
    .attr('class', 'axis')
    .call(d3.axisBottom(x));

  svg
    .append('text')
    .attr('text-anchor', 'end')
    .attr('transform', 'rotate(-90)')
    .style('fill', 'var(--p-card-color)')
    .attr('y', -spec.margin.left + 20)
    .attr('x', -spec.margin.top)
    .text('Instability');

  svg.append('g').attr('class', 'axis').call(d3.axisLeft(y));

  return svg;
}

/**
 * Determines the zone for the given measurement.
 * @param measurement The measurement.
 * @returns {{color: string, upperBound: number, name: string, lowerBound: number}}
 */
function determineZone(measurement: ComponentDependency) {
  let match = green;

  for (const zone of zones) {
    if (measurement.normalizedDistanceFromMainSequence <= zone.upperBound) {
      match = zone;
    }
  }

  return match;
}

/**
 * Determines the color for the measurement.
 * @returns {function(*): string}
 */
function determineColor() {
  return (d: ComponentDependency) => determineZone(d).color;
}

function renderGraph(
  svg: d3.Selection<SVGSVGElement, undefined, null, undefined>,
  metrics: ComponentDependency[],
  width: number,
  height: number
) {
  const x = d3.scaleLinear().domain([0, 1]).range([0, width]);
  const y = d3.scaleLinear().domain([0, 1]).range([height, 0]);

  svg
    .append('g')
    .selectAll('dot')
    .data(metrics)
    .enter()
    .append('circle')
    .attr('cx', (d: ComponentDependency) => x(d.instability))
    .attr('cy', (d: ComponentDependency) => y(d.abstractness))
    .attr('r', 7)
    .style('opacity', 0.3)
    .style('fill', determineColor())
    .append('svg:title')
    .text(
      (d: ComponentDependency, i: number) =>
        `${d.packageName}\nInstability: ${d.instability}\nAbstractness: ${d.abstractness}\nDistance: ${d.normalizedDistanceFromMainSequence}\nCa: ${d.afferentCoupling}\nCe: ${d.efferentCoupling}`
    );
}

@Component({
  selector: 'app-component-dependency-metrics-diagram',
  standalone: true,
  imports: [],
  template: `<figure #cdmmetrics></figure>`,
})
export class ComponentDependencyMetricsDiagramComponent {
  readonly metrics = input<ComponentDependency[]>([]);

  protected readonly figure = viewChild('cdmmetrics', {
    read: ElementRef,
  });

  readonly #totalSize = computed(() => 800);
  readonly #margin = computed(() => ({
    top: 10,
    right: 30,
    bottom: 40,
    left: 60,
  }));
  readonly #width = computed(
    () => this.#totalSize() - this.#margin().left - this.#margin().right
  );
  readonly #height = computed(
    () => this.#totalSize() - this.#margin().top - this.#margin().bottom
  );
  readonly #spec = computed(() => ({
    width: this.#width(),
    height: this.#height(),
    margin: this.#margin(),
  }));

  constructor() {
    afterRender({
      write: () => {
        const figure = this.figure();
        if (figure == null) {
          return;
        }

        const spec = this.#spec();
        const metrics = this.metrics();

        const svg = createSvg(spec);
        renderGraph(svg, metrics, spec.width, spec.height);

        figure.nativeElement.replaceChildren(svg.node());
      },
    });
  }
}
