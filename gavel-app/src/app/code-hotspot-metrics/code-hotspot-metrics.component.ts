import {Component, computed, inject, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {toSignal} from "@angular/core/rxjs-interop";
import CodeHotspotMetricsService, {CodeHotspot} from "./code-hotspot-metrics.service";
import {CardModule} from "primeng/card";
import {SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";

@Component({
  selector: 'app-code-hotspot-metrics',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    SharedModule,
    TableModule
  ],
  templateUrl: './code-hotspot-metrics.component.html',
  styleUrl: './code-hotspot-metrics.component.css'
})
export class CodeHotspotMetricsComponent {
  #service: CodeHotspotMetricsService = inject(CodeHotspotMetricsService);

  protected readonly metrics: Signal<CodeHotspot[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });
}
