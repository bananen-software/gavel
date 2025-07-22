import {Component} from '@angular/core';
import {CardModule} from "primeng/card";
import {ButtonModule} from "primeng/button";
import {ViewLayoutComponent} from "../view-layout/view-layout.component";
import {
  PackageComplexityDashboardComponent
} from "../package-complexity-dashboard.component/package-complexity-dashboard.component.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CardModule,
    ButtonModule,
    ViewLayoutComponent,
    PackageComplexityDashboardComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
