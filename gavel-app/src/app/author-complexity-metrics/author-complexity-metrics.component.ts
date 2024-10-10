import {Component, computed, inject, Signal} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {CardModule} from "primeng/card";
import {SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";
import {toSignal} from "@angular/core/rxjs-interop";
import AuthorComplexityMetricsService, {AuthorComplexity} from "./author-complexity-metrics.service";

@Component({
  selector: 'app-author-complexity-metrics',
  standalone: true,
  imports: [
    ViewLayoutComponent,
    CardModule,
    SharedModule,
    TableModule
  ],
  templateUrl: './author-complexity-metrics.component.html',
  styleUrl: './author-complexity-metrics.component.css'
})
export class AuthorComplexityMetricsComponent {
  #service: AuthorComplexityMetricsService = inject(AuthorComplexityMetricsService);

  protected readonly metrics: Signal<AuthorComplexity[]> = toSignal(this.#service.loadMetrics(), {
    initialValue: []
  })

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });
}
