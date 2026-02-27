import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth'; // Ensure this path matches your auth file

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Inject the AuthService dynamically
  const authService = inject(AuthService);
  const token = authService.getToken();

  // If we have a token, attach it to the request headers
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Send the cloned request with the token attached
    return next(clonedRequest);
  }

  // If no token (like logging in or registering), just send the normal request
  return next(req);
};