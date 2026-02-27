import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlobalPlayer } from './global-player';

describe('GlobalPlayer', () => {
  let component: GlobalPlayer;
  let fixture: ComponentFixture<GlobalPlayer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GlobalPlayer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GlobalPlayer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
