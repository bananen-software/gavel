import {afterRender, Component, input} from '@angular/core';
import * as d3 from 'd3';
import {ComponentDependency} from "../component-dependency-metrics/component-dependency-metrics.service";

class Zone {
  constructor(public upperBound: number,
              public lowerBound: number,
              public color: string,
              public name: "Green" | "Yellow" | "Red") {
  }

}

const green = new Zone(
  0.3,
  0,
  "green",
  "Green"
);

const yellow = new Zone(
  0.6,
  0.3,
  "yellow",
  "Yellow"
);

const red = new Zone(
  1,
  0.6,
  "red",
  "Red"
);

const zones: Zone[] = [red, yellow, green];

@Component({
  selector: 'app-component-dependency-metrics-diagram',
  standalone: true,
  imports: [],
  templateUrl: './component-dependency-metrics-diagram.component.html',
  styleUrl: './component-dependency-metrics-diagram.component.css'
})
export class ComponentDependencyMetricsDiagramComponent {
  readonly metrics = input<ComponentDependency[]>([]);

  private svg: any;
  private margin = {top: 10, right: 30, bottom: 40, left: 60};
  private width = 800 - this.margin.left - this.margin.right;
  private height = 800 - this.margin.top - this.margin.bottom;

  constructor() {
    afterRender({
      write: () => {
        if (this.svg == null) {
          this.createSvg();
        } else {
          this.svg.innerHTML = ''
        }

        this.renderGraph();
      }
    })
  }

  private createSvg(): void {
    this.svg = d3.select("figure#cdm-metrics")
      .append("svg")
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom)
      .append("g")
      .attr("transform",
        "translate(" + this.margin.left + "," + this.margin.top + ")");

    const x = d3.scaleLinear().domain([0, 1]).range([0, this.width])

    this.svg.append("text")
      .attr("text-anchor", "end")
      .attr("x", this.width)
      .attr("y", this.height + this.margin.top + 25)
      .style("fill", "var(--p-card-color)")
      .text("Abstractness");

    this.svg.append("g")
      .attr("transform", "translate(0," + this.height + ")")
      .attr("class", "axis")
      .call(d3.axisBottom(x));

    const y = d3.scaleLinear().domain([0, 1]).range([this.height, 0]);

    this.svg.append("text")
      .attr("text-anchor", "end")
      .attr("transform", "rotate(-90)")
      .style("fill", "var(--p-card-color)")
      .attr("y", -this.margin.left + 20)
      .attr("x", -this.margin.top)
      .text("Instability")

    this.svg.append("g")
      .attr("class", "axis")
      .call(d3.axisLeft(y));
  }

  private renderGraph() {
    const x = d3.scaleLinear().domain([0, 1]).range([0, this.width])
    const y = d3.scaleLinear().domain([0, 1]).range([this.height, 0]);

    this.svg.append('g')
      .selectAll("dot")
      .data(this.metrics())
      .enter()
      .append("circle")
      .attr("cx", function (d: ComponentDependency) {
        return x(d.instability);
      })
      .attr("cy", function (d: ComponentDependency) {
        return y(d.abstractness);
      })
      .attr("r", 7)
      .style("opacity", 0.3)
      .style("fill", this.determineColor())
      .append("svg:title")
      .text((d: ComponentDependency, i: number) =>
        `${d.packageName}\nInstability: ${d.instability}\nAbstractness: ${d.abstractness}\nDistance: ${d.normalizedDistanceFromMainSequence}\nCa: ${d.afferentCoupling}\nCe: ${d.efferentCoupling}`)
  }

  /**
   * Determines the color for the measurement.
   * @returns {function(*): string}
   */
  determineColor() {
    const component = this;

    return function (d: ComponentDependency) {
      return component.determineZone(d).color;
    };
  }

  /**
   * Determines the zone for the given measurement.
   * @param measurement The measurement.
   * @returns {{color: string, upperBound: number, name: string, lowerBound: number}}
   */
  determineZone(measurement: ComponentDependency) {
    let match = green;

    for (const zone of zones) {
      if (measurement.normalizedDistanceFromMainSequence <= zone.upperBound) {
        match = zone;
      }
    }

    return match;
  }
}
