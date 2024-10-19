import {Component, computed, inject, Signal} from '@angular/core';
import {CardModule} from "primeng/card";
import {SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {toSignal} from "@angular/core/rxjs-interop";
import PackageClassesOverviewService, {PackageClass} from "./package-classes-overview.service";
import {ActivatedRoute} from "@angular/router";
import {catchError, map, of, switchMap} from "rxjs";
import {toPercent, toPercentString} from "../../util/math-helpers";

@Component({
  selector: 'app-package-classes-overview',
  standalone: true,
  imports: [
    CardModule,
    SharedModule,
    TableModule,
    ViewLayoutComponent
  ],
  templateUrl: './package-classes-overview.component.html',
  styleUrl: './package-classes-overview.component.css'
})
export class PackageClassesOverviewComponent {

  #service: PackageClassesOverviewService = inject(PackageClassesOverviewService);
  private route = inject(ActivatedRoute);

  protected readonly metrics: Signal<PackageClass[]> =
    toSignal(this.route.paramMap.pipe(map(params => params.get("packageName")),
        switchMap(packageName => this.#service.loadMetrics(packageName)
          .pipe(catchError(error => {
            console.error(error);
            return of([]);
          })))),
      {
        initialValue: []
      });

  protected readonly loading: Signal<boolean> =
    computed(() => this.metrics.length > 0);

  protected readonly toPercent = toPercent;
  protected readonly toPercentString = toPercentString;
}
