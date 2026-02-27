import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyMusic } from './my-music';

describe('MyMusic', () => {
  let component: MyMusic;
  let fixture: ComponentFixture<MyMusic>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyMusic]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyMusic);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
