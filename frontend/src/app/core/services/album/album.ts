import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../auth/auth'; 

@Injectable({
  providedIn: 'root'
})
export class Album {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/albums`;

  // Helper method to attach the JWT token
  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  // NEW: Get All Albums (Public)
  getAllAlbums(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  // --- View a single album and its tracklist (Public) ---
  getAlbumById(albumId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${albumId}`, { headers: this.getHeaders() });
  }

  // --- Create Album (Artist Only) -  UPDATED FOR FORMDATA ---
  createAlbum(albumData: FormData): Observable<any> {
    return this.http.post<any>(this.apiUrl, albumData, { headers: this.getHeaders() });
  }

  // --- View My Albums (Artist Only) ---
  getMyAlbums(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my`, { headers: this.getHeaders() });
  }

  // --- Update Album (Artist Only) - UPDATED FOR FORMDATA ---
  updateAlbum(albumId: number, albumData: FormData): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${albumId}`, albumData, { headers: this.getHeaders() });
  }

  // --- Delete Album (Artist Only) ---
  deleteAlbum(albumId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${albumId}`, { headers: this.getHeaders() });
  }

  // --- Add Song to Album (Artist Only) ---
  addSongToAlbum(albumId: number, songId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${albumId}/songs/${songId}`, {}, { headers: this.getHeaders() });
  }

  // --- Remove Song from Album (Artist Only) ---
  removeSongFromAlbum(albumId: number, songId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${albumId}/songs/${songId}`, { headers: this.getHeaders() });
  }
}