import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentDependencyMetricsDiagramComponent } from './component-dependency-metrics-diagram.component';

describe('ComponentDependencyMetricsDiagramComponent', () => {
  let component: ComponentDependencyMetricsDiagramComponent;
  let fixture: ComponentFixture<ComponentDependencyMetricsDiagramComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentDependencyMetricsDiagramComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComponentDependencyMetricsDiagramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
