import {Component, computed, inject, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import RelationalCohesionMetricsService, {
  RelationalCohesion,
  RelationalCohesionLevel
} from "./relational-cohesion-metrics.service";
import {CardModule} from "primeng/card";
import {TableModule} from "primeng/table";
import {toSignal} from "@angular/core/rxjs-interop";
import {calculatePercentString} from "../../util/math-helpers";
import {IconFieldModule} from "primeng/iconfield";
import {InputIconModule} from "primeng/inputicon";
import {InputTextModule} from "primeng/inputtext";

class KeyInsights {
  constructor(public okCount: number,
              public goodPercentage: string,
              public lowCohesionCount: number,
              public lowCohesionPercentage: string,
              public highCohesionCount: number,
              public highCohesionPercentage: string,
              public totalCount: number) {
  }
}

@Component({
  selector: 'app-relational-cohesion-metrics',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    TableModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule
  ],
  templateUrl: './relational-cohesion-metrics.component.html',
  styleUrl: './relational-cohesion-metrics.component.css'
})
export class RelationalCohesionMetricsComponent {
  #service: RelationalCohesionMetricsService = inject(RelationalCohesionMetricsService);

  protected readonly metrics: Signal<RelationalCohesion[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });

  protected readonly keyInsights: Signal<KeyInsights> = computed(() => {
    //TODO: Refactor
    let totalCount: number = this.metrics().length;

    if (totalCount == 0) {
      return new KeyInsights(0, "0%", 0, "0%", 0, "0%", 0);
    } else {
      let goodCohesionCount: number = 0;
      let lowCohesionCount: number = 0;
      let highCohesionCount: number = 0;

      this.metrics()
        .map(metric => this.#service.determineRelationalCohesionLevel(metric))
        .forEach(cohesionLevel => {
          switch (cohesionLevel) {
            case RelationalCohesionLevel.Good:
              goodCohesionCount++;
              break;
            case RelationalCohesionLevel.Low:
              lowCohesionCount++;
              break;
            case RelationalCohesionLevel.High:
              highCohesionCount++;
              break;
          }
        });

      return new KeyInsights(
        goodCohesionCount,
        calculatePercentString(goodCohesionCount, totalCount),
        lowCohesionCount,
        calculatePercentString(lowCohesionCount, totalCount),
        highCohesionCount,
        calculatePercentString(highCohesionCount, totalCount),
        totalCount
      );
    }
  });

  public determineStatus(measurement: RelationalCohesion): string {
    //TODO: This should probably be part of the data loaded from the service
    return RelationalCohesionLevel[this.#service.determineRelationalCohesionLevel(measurement)];
  }
}
