import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CumulativeComponentDependencyMetricsComponent} from './cumulative-component-dependency-metrics.component';

describe('CumulativeComponentMetricsComponent', () => {
  let component: CumulativeComponentDependencyMetricsComponent;
  let fixture: ComponentFixture<CumulativeComponentDependencyMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CumulativeComponentDependencyMetricsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CumulativeComponentDependencyMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
