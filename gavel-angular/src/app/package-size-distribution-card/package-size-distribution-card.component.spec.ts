import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackageSizeDistributionCardComponent } from './package-size-distribution-card.component';

describe('PackageSizeDistributionCardComponent', () => {
  let component: PackageSizeDistributionCardComponent;
  let fixture: ComponentFixture<PackageSizeDistributionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PackageSizeDistributionCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackageSizeDistributionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
