import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http'; 

// Notice the hyphen here instead of a dot to match your exact file name!
import { authInterceptor } from './core/interceptors/auth/auth-interceptor'; 

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), // This replaces the need for zone.js!
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])) 
  ]
};