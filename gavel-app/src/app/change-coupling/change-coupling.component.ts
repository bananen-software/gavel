import {Component, computed, inject, Signal} from '@angular/core';
import {CardModule} from "primeng/card";
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {toSignal} from "@angular/core/rxjs-interop";
import ChangeCouplingService, {ChangeCoupling} from "./change-coupling.service";
import {TableModule} from "primeng/table";
import {toPercent, toPercentString} from "../../util/math-helpers";

@Component({
  selector: 'app-change-coupling',
  standalone: true,
  imports: [
    CardModule,
    ViewLayoutComponent,
    TableModule
  ],
  templateUrl: './change-coupling.component.html',
  styleUrl: './change-coupling.component.css'
})
export class ChangeCouplingComponent {
  #service: ChangeCouplingService = inject(ChangeCouplingService);

  protected readonly metrics: Signal<ChangeCoupling[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });
  protected readonly toPercentString = toPercentString;
  protected readonly toPercent = toPercent;
}
