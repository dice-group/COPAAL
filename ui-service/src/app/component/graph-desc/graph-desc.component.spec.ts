import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GraphDescComponent } from './graph-desc.component';

describe('GraphDescComponent', () => {
  let component: GraphDescComponent;
  let fixture: ComponentFixture<GraphDescComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GraphDescComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GraphDescComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
