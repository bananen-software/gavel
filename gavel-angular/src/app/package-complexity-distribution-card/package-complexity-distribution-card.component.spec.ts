import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackageComplexityDistributionCardComponent } from './package-complexity-distribution-card.component';

describe('PackageComplexityDistributionCardComponent', () => {
  let component: PackageComplexityDistributionCardComponent;
  let fixture: ComponentFixture<PackageComplexityDistributionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PackageComplexityDistributionCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackageComplexityDistributionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
