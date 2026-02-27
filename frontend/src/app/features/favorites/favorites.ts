import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { Song } from '../../core/services/song/song'; 
import { AudioService } from '../../core/services/audio/audio'; 
import { History } from '../../core/services/history/history'; 
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './favorites.html',  
  styleUrl: './favorites.css'      
})
export class Favorites implements OnInit {
  private songService = inject(Song);
  private router = inject(Router);
  
  private audioService = inject(AudioService);
  private historyService = inject(History);
  
  // NEW: This forces Angular to instantly refresh the HTML when the song changes
  private cdr = inject(ChangeDetectorRef); 

  favoriteSongs: any[] = []; 

  currentPlayingSongId: number | null = null;
  isCurrentlyPlaying: boolean = false;

  ngOnInit() {
    this.fetchFavorites();

    this.audioService.playerState$.subscribe(state => {
      if (state && state.song) {
        this.currentPlayingSongId = state.song.songId;
        this.isCurrentlyPlaying = state.isPlaying;
      } else {
        this.currentPlayingSongId = null;
        this.isCurrentlyPlaying = false;
      }
      // THE FIX: Tell HTML to update the UI immediately!
      this.cdr.detectChanges(); 
    });
  }

  fetchFavorites() {
    this.songService.getLikedSongs().subscribe({
      next: (data: any[]) => this.favoriteSongs = data,
      error: (err: any) => console.error('Failed to load favorites:', err)
    });
  }

  unlikeSong(event: Event, songId: number) {
    event.stopPropagation(); 
    this.songService.toggleLike(songId).subscribe({
      next: () => {
        this.favoriteSongs = this.favoriteSongs.filter((song: any) => song.songId !== songId);
      },
      error: (err: any) => console.error('Failed to unlike song:', err)
    });
  }

  playSong(song: any) {
    const mappedQueue = this.favoriteSongs.map((s: any) => ({
      songId: s.songId,
      title: s.title || s.songTitle,
      artistName: s.artistName || 'Unknown Artist',
      audioFileUrl: s.audioFileUrl || null,
      coverImageUrl: s.coverImageUrl || null 
    }));

    // THE FIX: Using loose equality (==) just in case one is a string and one is a number
    const songToPlay = mappedQueue.find((s: any) => s.songId == song.songId);

    if (songToPlay) {
      const audioEl = document.querySelector('audio');
      const isSameSong = this.currentPlayingSongId == song.songId;
      const isFinished = audioEl ? audioEl.ended : false;

      this.audioService.playSong(songToPlay, mappedQueue);

      if (!isSameSong || isFinished) {
        this.songService.incrementPlayCount(song.songId).subscribe({
          next: () => song.playCount = (song.playCount || 0) + 1,
          error: (err: any) => console.error('Failed to update play count:', err)
        });

        this.historyService.logPlay(song.songId).subscribe({
          error: (err: any) => console.error('Failed to log history:', err)
        });
      }
    }
  }

  // NEW: Helper to check if ANY favorite is currently playing for the big button
  isFavoritePlaying(): boolean {
    return this.isCurrentlyPlaying && this.favoriteSongs.some((s: any) => s.songId == this.currentPlayingSongId);
  }

  // UPDATED: Smart toggle for the big Play All button
  playAllFavorites() {
    if (this.favoriteSongs.length === 0) return;

    if (this.isFavoritePlaying()) {
      // If a favorite is actively playing, clicking the big button pauses it!
      const activeSong = this.favoriteSongs.find((s: any) => s.songId == this.currentPlayingSongId);
      if (activeSong) this.playSong(activeSong);
    } else {
      // Otherwise, start from the first track
      this.playSong(this.favoriteSongs[0]);
    }
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return 'assets/default-cover.jpg'; 
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }
}