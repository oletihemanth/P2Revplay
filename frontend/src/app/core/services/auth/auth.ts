import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment'; 
import { AuthResponse } from '../../models/auth-response.model';  

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response && response.token) {
          this.saveToken(response.token);
        }
        if (response && response.role) {
          this.saveRole(response.role);
        }
      })
    );
  }

  register(userData: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, userData, { responseType: 'text' });
  }

  // NEW: Forgot Password Method
  forgotPassword(email: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, { email, newPassword });
  }

  // --- Storage Helpers ---
  private saveToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  private saveRole(role: string): void {
    localStorage.setItem('user_role', role);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  getRole(): string | null {
    return localStorage.getItem('user_role');
  }

  getUserName(): string | null {
    return localStorage.getItem('userName'); 
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
    console.log('User logged out, storage cleared.');
  }
}