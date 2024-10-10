import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisibilityMetricsComponent } from './visibility-metrics.component';

describe('VisibilityMetricsComponent', () => {
  let component: VisibilityMetricsComponent;
  let fixture: ComponentFixture<VisibilityMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VisibilityMetricsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VisibilityMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
