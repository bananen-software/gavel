import {Component, computed, inject, input, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {toSignal} from "@angular/core/rxjs-interop";
import {PackageOverview, PackageOverviewService} from "./package-overview.service";
import {CardModule} from "primeng/card";
import {SharedModule} from "primeng/api";
import {TableModule, TableRowSelectEvent} from "primeng/table";
import {toPercent, toPercentString} from "../../util/math-helpers";
import {Router} from "@angular/router";

@Component({
  selector: 'app-package-overview',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    SharedModule,
    TableModule
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

  // TODO: Convert to pipe
  protected readonly toPercentString = toPercentString;
  protected readonly toPercent = toPercent;

  viewPackageDetail($event: TableRowSelectEvent) {
    this.#router.navigate(['/package-classes-overview/', $event.data.packageName]);
  }
}
