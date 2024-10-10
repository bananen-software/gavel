import {Component} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";

@Component({
  selector: 'app-project-settings',
  standalone: true,
  imports: [
    ViewLayoutComponent
  ],
  templateUrl: './project-settings.component.html',
  styleUrl: './project-settings.component.css'
})
export class ProjectSettingsComponent {

}
