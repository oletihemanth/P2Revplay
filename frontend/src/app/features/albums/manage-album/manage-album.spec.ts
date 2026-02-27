import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageAlbum } from './manage-album';

describe('ManageAlbum', () => {
  let component: ManageAlbum;
  let fixture: ComponentFixture<ManageAlbum>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageAlbum]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageAlbum);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
