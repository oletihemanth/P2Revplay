import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArtistAnalytics } from './artist-analytics';

describe('ArtistAnalytics', () => {
  let component: ArtistAnalytics;
  let fixture: ComponentFixture<ArtistAnalytics>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistAnalytics]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ArtistAnalytics);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
