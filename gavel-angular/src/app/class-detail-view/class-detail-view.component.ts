import {Component, computed, inject, Signal} from '@angular/core';
import {toSignal} from "@angular/core/rxjs-interop";
import ClassDetailViewService from "./class-detail-view.service";
import {catchError, map, of, switchMap} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {MenuItem} from "primeng/api";
import {
  BreadcrumbsComponent,
  classDetailView,
  home,
  packageClassesOverview,
  packageOverview
} from "../breadcrumbs/breadcrumbs.component";
import {ViewLayoutComponent} from "../view-layout/view-layout.component";

@Component({
  selector: 'app-class-detail-view',
  standalone: true,
  imports: [
    BreadcrumbsComponent,
    ViewLayoutComponent
  ],
  templateUrl: './class-detail-view.component.html',
  styleUrl: './class-detail-view.component.css'
})
export class ClassDetailViewComponent {

  #service: ClassDetailViewService = inject(ClassDetailViewService);
  private route = inject(ActivatedRoute);

  protected readonly metrics: Signal<any | null> =
    toSignal(this.route.paramMap.pipe(map(params => params.get("classId")),
      switchMap(classId => this.#service.loadMetrics(classId)
        .pipe(catchError(error => {
          console.error(error);
          return of();
        })))));

  protected readonly loading: Signal<boolean> = computed(() => {
    return this.metrics().length == 0;
  });

  protected readonly breadcrumbs: Signal<MenuItem[]> =
    computed(() => {
      if (this.metrics()) {
        return [
          home,
          packageOverview,
          packageClassesOverview(this.metrics().classById.package.id ?? '', this.metrics().classById.package.name ?? ''),
          classDetailView(this.route.snapshot.paramMap.get("classId") ?? '', this.metrics().classById.name ?? '')
        ]
      } else {
        return []
      }
    });
}
