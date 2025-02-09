import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CodeHotspotMetricsComponent } from './code-hotspot-metrics.component';

describe('CodeHotspotMetricsComponent', () => {
  let component: CodeHotspotMetricsComponent;
  let fixture: ComponentFixture<CodeHotspotMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CodeHotspotMetricsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CodeHotspotMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
