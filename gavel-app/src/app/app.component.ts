import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {MenubarModule} from "primeng/menubar";
import {MenuItem, PrimeIcons, PrimeNGConfig} from "primeng/api";
import {Aura} from 'primeng/themes/aura';
import {ViewLayoutComponent} from "./view-layout/view-layout.component";
import {ButtonModule} from "primeng/button";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MenubarModule,
    ViewLayoutComponent,
    ButtonModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'gavel-app';
  items: MenuItem[] | undefined;

  constructor(private config: PrimeNGConfig,
              private httpClient: HttpClient) {
    this.config.theme.set({
      preset: Aura
    });
  }

  runAnalysis(): void {
    this.httpClient.post("http://localhost:8080/tasks", {})
      .subscribe(res => console.log(res))
  }

  ngOnInit() {
    this.items = [
      {
        label: 'Dashboard',
        icon: PrimeIcons.HOME,
        routerLink: "/"
      },
      {
        label: 'Static Analysis',
        icon: PrimeIcons.CHART_PIE,
        items: [
          {
            label: 'Component Dependency Metrics',
            icon: PrimeIcons.SITEMAP,
            routerLink: "component-dependency-metrics"
          },
          {
            label: 'Cumulative Component Dependency Metrics',
            icon: PrimeIcons.SERVER,
            routerLink: "cumulative-component-dependency-metrics"
          },
          {
            label: 'Relational Cohesion Metrics',
            icon: PrimeIcons.PENCIL,
            routerLink: "relational-cohesion-metrics"
          },
          {
            label: 'Visibility Metrics',
            icon: PrimeIcons.PENCIL,
            routerLink: "visibility-metrics"
          },
          {
            label: 'Package Overview',
            icon: PrimeIcons.PENCIL,
            routerLink: "package-overview"
          }
        ]
      },
      {
        label: 'Behavioral Analysis',
        icon: PrimeIcons.COMPASS,
        items: [
          {
            label: 'Author Complexity Metrics',
            icon: PrimeIcons.USER,
            routerLink: "author-complexity-metrics"
          },
          {
            label: 'Code Hotspot Metrics',
            icon: PrimeIcons.LIGHTBULB,
            routerLink: "code-hotspot-metrics"
          },
        ]
      },
      {
        label: 'Runtime Analysis',
        icon: PrimeIcons.CLOCK,
        items: [
          {
            label: 'Runtime dependencies',
            icon: PrimeIcons.OBJECTS_COLUMN,
            routerLink: "runtime-dependencies"
          }
        ]
      },
      {
        label: 'Settings',
        icon: PrimeIcons.COG,
        routerLink: "project-settings"
      }
    ]
  }
}
