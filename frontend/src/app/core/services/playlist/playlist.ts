import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../auth/auth'; 

@Injectable({
  providedIn: 'root'
})
export class Playlist { 
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/playlists`;

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  getPlaylistById(playlistId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${playlistId}`, { headers: this.getHeaders() });
  }

  // UPDATED: Accepts FormData
  createPlaylist(playlistData: FormData): Observable<any> {
    return this.http.post<any>(this.apiUrl, playlistData, { headers: this.getHeaders() });
  }

  getMyPlaylists(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/me`, { headers: this.getHeaders() });
  }

  getPublicPlaylists(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/public`, { headers: this.getHeaders() });
  }

  //  UPDATED: Accepts FormData
  updatePlaylist(playlistId: number, playlistData: FormData): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${playlistId}`, playlistData, { headers: this.getHeaders() });
  }

  deletePlaylist(playlistId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${playlistId}`, { headers: this.getHeaders(), responseType: 'text' as 'json' });
  }

  addSongToPlaylist(playlistId: number, songId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${playlistId}/songs/${songId}`, {}, { headers: this.getHeaders(), responseType: 'text' as 'json' });
  }

  removeSongFromPlaylist(playlistId: number, songId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${playlistId}/songs/${songId}`, { headers: this.getHeaders(), responseType: 'text' as 'json' });
  }

  toggleFollowPlaylist(playlistId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${playlistId}/follow`, {}, { headers: this.getHeaders(), responseType: 'text' as 'json' });
  }
}