import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../auth/auth'; 

@Injectable({
  providedIn: 'root'
})
export class Song {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/songs`;

  getAllSongs(): Observable<any[]> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(this.apiUrl, { headers });
  }

  uploadSong(songData: FormData): Observable<any> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(this.apiUrl, songData, { headers });
  }

  searchSongsByTitle(title: string): Observable<any[]> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/search?title=${title}`, { headers });
  }

  filterSongsByGenre(genre: string): Observable<any[]> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/filter?genre=${genre}`, { headers });
  }

  toggleLike(songId: number): Observable<boolean> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post<boolean>(`${this.apiUrl}/${songId}/like`, {}, { headers });
  }

  getLikedSongs(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/liked`, { headers });
  }

  getMyUploadedSongs(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/my-songs`, { headers });
  }

  deleteSong(songId: number): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.delete(`${this.apiUrl}/${songId}`, { headers, responseType: 'text' as 'json' });
  }

  updateSong(songId: number, title: string, genre: string, visibility: string): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const url = `${this.apiUrl}/${songId}?title=${encodeURIComponent(title)}&genre=${encodeURIComponent(genre)}&visibility=${encodeURIComponent(visibility)}`;
    return this.http.put(url, {}, { headers });
  }

  // --- NEW: Increment Play Count ---
  incrementPlayCount(songId: number): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(`${this.apiUrl}/${songId}/increment-play`, {}, { headers, responseType: 'text' as 'json' });
  }
}