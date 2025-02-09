import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelationalCohesionMetricsComponent } from './relational-cohesion-metrics.component';

describe('RelationalCohesionMetricsComponent', () => {
  let component: RelationalCohesionMetricsComponent;
  let fixture: ComponentFixture<RelationalCohesionMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RelationalCohesionMetricsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RelationalCohesionMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
