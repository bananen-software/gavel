import {Component, input} from '@angular/core';
import {BreadcrumbModule} from "primeng/breadcrumb";
import {NgClass, NgIf} from "@angular/common";
import {MenuItem, SharedModule} from "primeng/api";

export const home: MenuItem = {
  label: 'Home',
  routerLink: ['/'],
  icon: 'pi pi-home'
};

export const packageOverview: MenuItem = {
  label: 'Packages',
  routerLink: ['/package-overview'],
  icon: 'pi pi-folder-open'
};

export function packageClassesOverview(packageId: string, packageName: string): MenuItem {
  return {
    label: packageName,
    routerLink: ['/package-classes-overview', packageId],
    icon: undefined
  }
}

export function classDetailView(classId: string, className: string): MenuItem {
  return {
    label: className,
    routerLink: ['/class-detail-view', classId],
    icon: undefined
  }
}

@Component({
  selector: 'app-breadcrumbs',
  standalone: true,
  imports: [
    BreadcrumbModule,
    NgIf,
    SharedModule,
    NgClass
  ],
  templateUrl: './breadcrumbs.component.html',
  styleUrl: './breadcrumbs.component.css'
})
export class BreadcrumbsComponent {
  readonly breadcrumbs = input<MenuItem[]>([]);
}
