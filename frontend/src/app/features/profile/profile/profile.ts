import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth'; 
import { environment } from '../../../../environments/environment'; 

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  userProfile: any = null;
  userStats: any = null;
  
  newName: string = '';
  isEditing: boolean = false;

  ngOnInit() {
    this.fetchProfile();
    this.fetchStats();
  }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  }

  fetchProfile() {
    this.http.get(`${environment.apiUrl}/users/me`, { headers: this.getHeaders() }).subscribe({
      next: (data: any) => {
        this.userProfile = data;
        this.newName = data.name; // Pre-fill the edit input
      },
      error: (err) => console.error('Failed to load profile', err)
    });
  }

  fetchStats() {
    this.http.get(`${environment.apiUrl}/users/me/stats`, { headers: this.getHeaders() }).subscribe({
      next: (data: any) => {
        this.userStats = data;
      },
      error: (err) => console.error('Failed to load stats', err)
    });
  }

  updateName() {
    if (!this.newName.trim()) {
      alert("Name cannot be empty!");
      return;
    }

    this.http.put(`${environment.apiUrl}/users/me/name?name=${encodeURIComponent(this.newName)}`, {}, { headers: this.getHeaders(), responseType: 'text' })
      .subscribe({
        next: () => {
          this.userProfile.name = this.newName;
          this.isEditing = false;
          // Update local storage so the header on other pages updates immediately
          localStorage.setItem('userName', this.newName);
        },
        error: (err) => {
          console.error('Failed to update name', err);
          alert("Failed to update name.");
        }
      });
  }
}