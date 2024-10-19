import {Component, computed, inject, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {CardModule} from "primeng/card";
import {TableModule} from "primeng/table";
import ComponentDependencyMetricsService, {
  ComponentDependency,
  DependencyZone
} from "./component-dependency-metrics.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {calculatePercentString, toPercent} from "../../util/math-helpers";
import {KnobModule} from "primeng/knob";
import {FormsModule} from "@angular/forms";
import {ProgressBarModule} from "primeng/progressbar";
import {MeterGroupModule} from "primeng/metergroup";
import {NgForOf} from "@angular/common";
import {
  ComponentDependencyMetricsDiagramComponent
} from "../component-dependency-metrics-diagram/component-dependency-metrics-diagram.component";

class KeyInsights {
  constructor(public grayAreaCount: number,
              public grayAreaPercentage: string,
              public grayAreaPercentageNumeric: number,
              public zoneOfUselessnessCount: number,
              public zoneOfUselessnessPercentage: string,
              public zoneOfUselessnessPercentageNumeric: number,
              public zoneOfPainCount: number,
              public zoneOfPainPercentage: string,
              public zoneOfPainPercentageNumeric: number,
              public totalCount: number) {
  }
}

@Component({
  selector: 'app-component-dependency-metrics',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    TableModule,
    KnobModule,
    FormsModule,
    ProgressBarModule,
    MeterGroupModule,
    NgForOf,
    ComponentDependencyMetricsDiagramComponent
  ],
  templateUrl: './component-dependency-metrics.component.html',
  styleUrl: './component-dependency-metrics.component.css'
})
export class ComponentDependencyMetricsComponent {
  #service: ComponentDependencyMetricsService = inject(ComponentDependencyMetricsService);

  protected readonly metrics: Signal<ComponentDependency[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly keyInsights: Signal<KeyInsights> = computed(() => {
    let metrics = this.metrics();

    if (metrics.length == 0) {
      return new KeyInsights(0, "0%", 0, 0, "0%", 0, 0, "0%", 0, 0);
    } else {
      const totalCount = metrics.length;
      let grayAreaCount = 0;
      let zoneOfPainCount = 0;
      let zoneOfUselessnessCount = 0;

      metrics.map(v => this.determineZone(v))
        .forEach(v => {
          switch (v) {
            case "GrayArea":
              grayAreaCount++;
              break;

            case "ZoneOfUselessness":
              zoneOfUselessnessCount++
              break;

            case "ZoneOfPain":
              zoneOfPainCount++;
              break;
          }
        });


      return new KeyInsights(grayAreaCount,
        calculatePercentString(grayAreaCount, totalCount),
        toPercent(grayAreaCount, totalCount),
        zoneOfUselessnessCount,
        calculatePercentString(zoneOfUselessnessCount, totalCount),
        toPercent(zoneOfUselessnessCount, totalCount),
        zoneOfPainCount,
        calculatePercentString(zoneOfPainCount, totalCount),
        toPercent(zoneOfPainCount, totalCount),
        totalCount);
    }
  });

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });


  public determineZone(measurement: ComponentDependency): string {
    return DependencyZone[this.#service.determineZone(measurement)];
  }

  protected readonly calculatePercentString = calculatePercentString;
}
