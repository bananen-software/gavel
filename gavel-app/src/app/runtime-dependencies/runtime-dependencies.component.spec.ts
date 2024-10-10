import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RuntimeDependenciesComponent } from './runtime-dependencies.component';

describe('RuntimeDependenciesComponent', () => {
  let component: RuntimeDependenciesComponent;
  let fixture: ComponentFixture<RuntimeDependenciesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RuntimeDependenciesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RuntimeDependenciesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
