import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscoverPlaylists } from './discover-playlists';

describe('DiscoverPlaylists', () => {
  let component: DiscoverPlaylists;
  let fixture: ComponentFixture<DiscoverPlaylists>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiscoverPlaylists]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DiscoverPlaylists);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
