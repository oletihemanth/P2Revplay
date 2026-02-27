import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../auth/auth';

@Injectable({
  providedIn: 'root'
})
export class History {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/history`;

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  }

  // Logs the play to the database
  logPlay(songId: number, playlistId?: number): Observable<any> {
    let url = `${this.apiUrl}/log?songId=${songId}`;
    if (playlistId) {
      url += `&playlistId=${playlistId}`;
    }
    return this.http.post(url, {}, { headers: this.getHeaders(), responseType: 'text' });
  }

  // Fetches your top 50 recent plays
  getRecentHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/recent`, { headers: this.getHeaders() });
  }
}