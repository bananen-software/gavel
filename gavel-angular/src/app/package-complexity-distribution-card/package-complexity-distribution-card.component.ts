import {Component, computed, input, Signal} from '@angular/core';
import {CardModule} from "primeng/card";
import {ChartModule} from "primeng/chart";
import {PackageComplexityPipe} from "../../pipes/PackageComplexityPipe";
import {PieChartData} from "../package-overview/package-overview.component";
import {PackageOverview} from "../package-overview/package-overview.service";

@Component({
  selector: 'app-package-complexity-distribution-card',
  standalone: true,
  imports: [
    CardModule,
    ChartModule
  ],
  templateUrl: './package-complexity-distribution-card.component.html',
  styleUrl: './package-complexity-distribution-card.component.css'
})
export class PackageComplexityDistributionCardComponent {
  readonly metrics = input<PackageOverview[]>([]);

  protected readonly complexityChartData: Signal<PieChartData> = computed(() => {
    const labels: string[] = [];
    const values: number[] = [];
    const backgroundColors: string[] = [];

    for (let metric of this.metrics()) {
      const packageComplexity = new PackageComplexityPipe().transform(metric.packageComplexity);
      let index: number = labels.indexOf(packageComplexity);

      if (index === -1) {
        index = 0;
        labels.push(packageComplexity);
        values.push(1);
        backgroundColors.push(this.mapComplexityToColor(metric.packageComplexity));
      } else {
        values[index] = values[index] + 1;
      }
    }

    return {
      labels: labels,
      datasets: [{
        data: values,
        backgroundColor: backgroundColors
      }]
    };
  });

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

  private mapComplexityToColor(complexity: string): string {
    switch (complexity) {
      case 'EMPTY':
        return this.computeCssVar('--p-gray-200');

      case 'MOSTLY_SIMPLE':
        return this.computeCssVar('--p-teal-600');

      case 'BALANCED':
        return this.computeCssVar('--p-teal-800');

      case 'COMPLEX':
        return this.computeCssVar('--p-orange-600');

      case 'HIGHLY_COMPLEX':
        return this.computeCssVar('--p-red-600');

      default:
        return "black"
    }
  }

  protected computeCssVar(variable: string) {
    return getComputedStyle(document.documentElement).getPropertyValue(variable);
  }
}
