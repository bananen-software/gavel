import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthorComplexityMetricsComponent } from './author-complexity-metrics.component';

describe('AuthorComplexityMetricsComponent', () => {
  let component: AuthorComplexityMetricsComponent;
  let fixture: ComponentFixture<AuthorComplexityMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorComplexityMetricsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthorComplexityMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
