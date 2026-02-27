import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyAlbums } from './my-albums';

describe('MyAlbums', () => {
  let component: MyAlbums;
  let fixture: ComponentFixture<MyAlbums>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyAlbums]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyAlbums);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
