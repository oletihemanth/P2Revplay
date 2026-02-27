import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth'; 

export const artistGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if they are logged in AND their role is exactly 'ARTIST'
  if (authService.isLoggedIn() && authService.getRole() === 'ARTIST') {
    return true; // Let them in!
  }

  // If they are just a regular user, kick them back to the home page
  router.navigate(['/']);
  return false;
};