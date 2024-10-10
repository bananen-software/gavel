import {Component, computed, inject, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {CardModule} from "primeng/card";
import {SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";
import VisibilityMetricsService, {Visibility} from "./visibility-metrics.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {toPercent, toPercentString} from "../../util/math-helpers";

@Component({
  selector: 'app-visibility-metrics',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    SharedModule,
    TableModule
  ],
  templateUrl: './visibility-metrics.component.html',
  styleUrl: './visibility-metrics.component.css'
})
export class VisibilityMetricsComponent {
  #service: VisibilityMetricsService = inject(VisibilityMetricsService);

  protected readonly metrics: Signal<Visibility[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });
  protected readonly toPercent = toPercent;
  protected readonly toPercentString = toPercentString;
}
