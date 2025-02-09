import {Component} from '@angular/core';
import {ViewLayoutComponent} from "../view-layout/view-layout.component";

@Component({
  selector: 'app-runtime-dependencies',
  standalone: true,
  imports: [
    ViewLayoutComponent
  ],
  templateUrl: './runtime-dependencies.component.html',
  styleUrl: './runtime-dependencies.component.css'
})
export class RuntimeDependenciesComponent {

}
