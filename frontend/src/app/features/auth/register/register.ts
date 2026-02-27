import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  private authService = inject(AuthService);
  private router = inject(Router);

  onRegister(event: Event) {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    
    // Extract exact values from the HTML form
    const nameInput = form.elements.namedItem('name') as HTMLInputElement;
    const emailInput = form.elements.namedItem('email') as HTMLInputElement;
    const passwordInput = form.elements.namedItem('password') as HTMLInputElement;
    const roleSelect = form.elements.namedItem('role') as HTMLSelectElement;

    const userData = {
      name: nameInput.value,
      email: emailInput.value,
      password: passwordInput.value,
      role: roleSelect.value
    };

    console.log('Registering user with data:', userData);

    this.authService.register(userData).subscribe({
      next: () => {
        alert('Registration Successful! Please Login.');
        this.router.navigate(['/login']);
      },
      error: (err) => alert('Registration Failed. Check console.')
    });
  }
}