import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { applyChartDefaults } from './app/charts/chart-defaults';

applyChartDefaults();

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
