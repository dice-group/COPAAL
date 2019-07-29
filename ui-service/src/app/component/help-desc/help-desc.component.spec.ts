import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HelpDescComponent } from './help-desc.component';

describe('HelpDescComponent', () => {
  let component: HelpDescComponent;
  let fixture: ComponentFixture<HelpDescComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HelpDescComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HelpDescComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
