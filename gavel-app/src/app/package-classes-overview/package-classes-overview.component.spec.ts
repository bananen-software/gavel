import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackageClassesOverviewComponent } from './package-classes-overview.component';

describe('PackageClassesOverviewComponent', () => {
  let component: PackageClassesOverviewComponent;
  let fixture: ComponentFixture<PackageClassesOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PackageClassesOverviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackageClassesOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
