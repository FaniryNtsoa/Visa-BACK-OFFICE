import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandeList } from './demande-list';

describe('DemandeList', () => {
  let component: DemandeList;
  let fixture: ComponentFixture<DemandeList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandeList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
