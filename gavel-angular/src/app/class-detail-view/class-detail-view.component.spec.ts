import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassDetailViewComponent } from './class-detail-view.component';

describe('ClassDetailViewComponent', () => {
  let component: ClassDetailViewComponent;
  let fixture: ComponentFixture<ClassDetailViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassDetailViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClassDetailViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
