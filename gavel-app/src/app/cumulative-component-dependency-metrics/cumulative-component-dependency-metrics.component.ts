import {Component, computed, inject, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {CardModule} from "primeng/card";
import {TableModule} from "primeng/table";
import CumulativeComponentDependencyMetricsService, {
  CumulativeComponentDependency
} from "./cumulative-component-dependency-metrics.service";
import {toSignal} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-cumulative-component-metrics',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    TableModule
  ],
  templateUrl: './cumulative-component-dependency-metrics.component.html',
  styleUrl: './cumulative-component-dependency-metrics.component.css'
})
export class CumulativeComponentDependencyMetricsComponent {
  #service: CumulativeComponentDependencyMetricsService = inject(CumulativeComponentDependencyMetricsService);

  protected readonly metrics: Signal<CumulativeComponentDependency[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });
}
