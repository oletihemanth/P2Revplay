import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth'; // <-- Changed from Auth to AuthService

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // We must provide the HttpClient here so the tests don't crash!
      providers: [provideHttpClient()] 
    });
    service = TestBed.inject(AuthService); // <-- Changed from Auth to AuthService
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});