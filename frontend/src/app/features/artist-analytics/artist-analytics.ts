import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router'; //  NEW: Import RouterLink
import { Song } from '../../core/services/song/song'; // Adjust path if needed
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-artist-analytics',
  standalone: true,
  imports: [CommonModule, RouterLink], //  NEW: Added RouterLink here
  templateUrl: './artist-analytics.html',
  styleUrl: './artist-analytics.css'
})
export class ArtistAnalytics implements OnInit {
  private songService = inject(Song);

  mySongs: any[] = [];
  totalLifetimePlays: number = 0;
  mostPopularSong: any = null;
  isLoading: boolean = true;

  ngOnInit() {
    this.fetchAnalytics();
  }

  fetchAnalytics() {
    // Calling the /my-songs endpoint you already have!
    this.songService.getMyUploadedSongs().subscribe({
      next: (songs: any[]) => {
        // 1. Save the songs
        this.mySongs = songs;

        // 2. Calculate Total Lifetime Plays
        this.totalLifetimePlays = this.mySongs.reduce((sum, song) => sum + (song.playCount || 0), 0);

        // 3. Find the Most Popular Song (sort by highest playCount)
        if (this.mySongs.length > 0) {
          const sortedSongs = [...this.mySongs].sort((a, b) => (b.playCount || 0) - (a.playCount || 0));
          this.mostPopularSong = sortedSongs[0];
          
          // Let's also sort the main list so the table shows top tracks first!
          this.mySongs = sortedSongs;
        }

        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Failed to load analytics:', err);
        this.isLoading = false;
      }
    });
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return 'assets/default-cover.jpg';
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }
}