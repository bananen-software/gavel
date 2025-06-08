import {Component, computed, inject, input, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {toSignal} from "@angular/core/rxjs-interop";
import {PackageOverview, PackageOverviewService} from "./package-overview.service";
import {CardModule} from "primeng/card";
import {MenuItem, SharedModule} from "primeng/api";
import {TableModule, TableRowSelectEvent} from "primeng/table";
import {Router} from "@angular/router";
import {BreadcrumbsComponent, home, packageOverview} from "../breadcrumbs/breadcrumbs.component";
import {DecimalPipe, PercentPipe} from "@angular/common";
import {ElementSizePipe} from "../../pipes/ElementSizePipe";
import {PackageComplexityPipe} from "../../pipes/PackageComplexityPipe";
import {ChartModule} from 'primeng/chart';
import {
  PackageComplexityDistributionCardComponent
} from "../package-complexity-distribution-card/package-complexity-distribution-card.component";
import {
  PackageSizeDistributionCardComponent
} from "../package-size-distribution-card/package-size-distribution-card.component";

export type PieChartDataSet = {
  data: number[];
};

export type PieChartData = {
  labels: string[],
  datasets: PieChartDataSet[]
};

@Component({
  selector: 'app-package-overview',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    SharedModule,
    TableModule,
    BreadcrumbsComponent,
    PercentPipe,
    DecimalPipe,
    ElementSizePipe,
    PackageComplexityPipe,
    ChartModule,
    PackageComplexityDistributionCardComponent,
    PackageSizeDistributionCardComponent
  ],
  templateUrl: './package-overview.component.html',
  styleUrl: './package-overview.component.css'
})
export class PackageOverviewComponent {

  #service: PackageOverviewService = inject(PackageOverviewService);
  #router = inject(Router);

  protected readonly selectedPackage = input<PackageOverview>();

  protected readonly metrics: Signal<PackageOverview[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });

  protected readonly breadcrumbs: Signal<MenuItem[]> =
    computed(() => [
      home,
      packageOverview
    ]);

  viewPackageDetail($event: TableRowSelectEvent) {
    console.log($event);
    this.#router.navigate(['/package-classes-overview/', $event.data.packageId]);
  }
}
