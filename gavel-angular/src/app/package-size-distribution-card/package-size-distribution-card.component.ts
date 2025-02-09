import {Component, computed, input, Signal} from '@angular/core';
import {ElementSizePipe} from "../../pipes/ElementSizePipe";
import {PieChartData} from "../package-overview/package-overview.component";
import {PackageOverview} from "../package-overview/package-overview.service";
import {CardModule} from "primeng/card";
import {ChartModule} from "primeng/chart";

@Component({
  selector: 'app-package-size-distribution-card',
  standalone: true,
  imports: [
    CardModule,
    ChartModule
  ],
  templateUrl: './package-size-distribution-card.component.html',
  styleUrl: './package-size-distribution-card.component.css'
})
export class PackageSizeDistributionCardComponent {
  readonly metrics = input<PackageOverview[]>([]);

  protected readonly sizeChartData: Signal<PieChartData> = computed(() => {
    const labels: string[] = [];
    const values: number[] = [];
    const backgroundColors: string[] = [];

    for (let metric of this.metrics()) {
      const size = new ElementSizePipe().transform(metric.size);
      let index: number = labels.indexOf(size);

      if (labels.indexOf(size) === -1) {
        index = 0;
        labels.push(size);
        values.push(0);
        backgroundColors.push(this.mapToColor(metric.size));
      }

      values[index] = values[index] + 1;
    }

    console.log(backgroundColors);

    return {
      labels: labels,
      datasets: [{
        data: values,
        backgroundColor: backgroundColors,
      }]
    };
  });

  private mapToColor(size: string): string {
    switch (size) {
      case 'UNKNOWN':
        return this.computeCssVar("--p-gray-200");

      case 'EMPTY':
        return this.computeCssVar("--p-gray-200");

      case 'SMALL':
        return this.computeCssVar('--p-teal-600');

      case 'MEDIUM':
        return this.computeCssVar('--p-orange-600');

      case 'LARGE':
        return this.computeCssVar("--p-red-600");

      case 'VERY_LARGE':
        return this.computeCssVar("--p-red-800");
    }

    return size;
  }

  protected readonly options =
    {
      plugins: {
        legend: {
          labels: {
            usePointStyle: true
          }
        }
      }
    };

  protected computeCssVar(variable: string) {
    return getComputedStyle(document.documentElement).getPropertyValue(variable);
  }
}
