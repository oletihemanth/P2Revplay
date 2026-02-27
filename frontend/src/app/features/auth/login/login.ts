import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; // Required for *ngIf
import { AuthService } from '../../../core/services/auth/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, CommonModule], // Added CommonModule
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);

  // View state
  showForgotPassword = false;

  toggleView() {
    this.showForgotPassword = !this.showForgotPassword;
  }

  onLogin(event: Event) {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    
    const emailInput = form.elements.namedItem('email') as HTMLInputElement;
    const passwordInput = form.elements.namedItem('password') as HTMLInputElement;

    const credentials = {
      email: emailInput.value,
      password: passwordInput.value
    };

    this.authService.login(credentials).subscribe({
      next: (res) => {
        if (res && res.token) {
          this.router.navigate(['/home']); 
        }
      },
      error: (err) => {
        alert('Login Failed: Check your credentials');
      }
    });
  }

  // Handle Password Reset
  onResetPassword(event: Event) {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    
    const emailInput = form.elements.namedItem('resetEmail') as HTMLInputElement;
    const newPasswordInput = form.elements.namedItem('newPassword') as HTMLInputElement;

    this.authService.forgotPassword(emailInput.value, newPasswordInput.value).subscribe({
      next: () => {
        alert('Password successfully reset! You can now log in.');
        this.toggleView(); // Go back to login screen
      },
      error: (err) => {
        alert(err.error?.error || 'Failed to reset password. Is the email correct?');
      }
    });
  }
}