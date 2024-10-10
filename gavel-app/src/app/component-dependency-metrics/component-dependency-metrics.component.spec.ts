import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentDependencyMetricsComponent } from './component-dependency-metrics.component';

describe('ComponentDependencyMetricsComponent', () => {
  let component: ComponentDependencyMetricsComponent;
  let fixture: ComponentFixture<ComponentDependencyMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentDependencyMetricsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComponentDependencyMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
