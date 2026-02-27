import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth'; // Ensure this matches your auth service file name

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true; // Let them in!
  }

  // Not logged in? Kick them to the login page
  router.navigate(['/login']);
  return false; 
};