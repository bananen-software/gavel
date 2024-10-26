import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeCouplingComponent } from './change-coupling.component';

describe('ChangeCouplingComponent', () => {
  let component: ChangeCouplingComponent;
  let fixture: ComponentFixture<ChangeCouplingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeCouplingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeCouplingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
