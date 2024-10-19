import {Routes} from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {
  ComponentDependencyMetricsComponent
} from "./component-dependency-metrics/component-dependency-metrics.component";
import {
  CumulativeComponentDependencyMetricsComponent
} from "./cumulative-component-dependency-metrics/cumulative-component-dependency-metrics.component";
import {RelationalCohesionMetricsComponent} from "./relational-cohesion-metrics/relational-cohesion-metrics.component";
import {RuntimeDependenciesComponent} from "./runtime-dependencies/runtime-dependencies.component";
import {VisibilityMetricsComponent} from "./visibility-metrics/visibility-metrics.component";
import {AuthorComplexityMetricsComponent} from "./author-complexity-metrics/author-complexity-metrics.component";
import {CodeHotspotMetricsComponent} from "./code-hotspot-metrics/code-hotspot-metrics.component";
import {ProjectSettingsComponent} from "./project-settings/project-settings.component";
import {PackageOverviewComponent} from "./package-overview/package-overview.component";
import {PackageClassesOverviewComponent} from "./package-classes-overview/package-classes-overview.component";

export const routes: Routes = [
  {
    path: "",
    component: DashboardComponent
  },
  {
    path: "component-dependency-metrics",
    component: ComponentDependencyMetricsComponent
  },
  {
    path: "cumulative-component-dependency-metrics",
    component: CumulativeComponentDependencyMetricsComponent
  },
  {
    path: "relational-cohesion-metrics",
    component: RelationalCohesionMetricsComponent
  },
  {
    path: "runtime-dependencies",
    component: RuntimeDependenciesComponent
  },
  {
    path: "visibility-metrics",
    component: VisibilityMetricsComponent
  },
  {
    path: "author-complexity-metrics",
    component: AuthorComplexityMetricsComponent
  },
  {
    path: "code-hotspot-metrics",
    component: CodeHotspotMetricsComponent
  },
  {
    path: "project-settings",
    component: ProjectSettingsComponent
  },
  {
    path: "package-overview",
    component: PackageOverviewComponent
  },
  {
    path: "package-classes-overview/:packageName",
    component: PackageClassesOverviewComponent
  }
];
