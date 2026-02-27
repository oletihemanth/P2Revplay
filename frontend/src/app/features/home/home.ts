import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { AuthService } from '../../core/services/auth'; 
import { Song } from '../../core/services/song/song'; 
import { Playlist } from '../../core/services/playlist/playlist'; 
import { History } from '../../core/services/history/history'; 
import { AudioService } from '../../core/services/audio/audio'; 
import { Album } from '../../core/services/album/album'; 
import { environment } from '../../../environments/environment'; 

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], 
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  private authService = inject(AuthService);
  private songService = inject(Song);
  private playlistService = inject(Playlist); 
  private historyService = inject(History); 
  private audioService = inject(AudioService); 
  private albumService = inject(Album); 
  private router = inject(Router);
  
  // NEW: Inject ChangeDetectorRef for instant UI updates
  private cdr = inject(ChangeDetectorRef);

  userName: string = '';
  userRole: string = '';
  songs: any[] = []; 
  albums: any[] = []; 
  
  searchQuery: string = '';
  selectedGenre: string = '';

  likedSongIds: Set<number> = new Set<number>();

  myPlaylists: any[] = [];
  showPlaylistModal: boolean = false;
  selectedSongForPlaylist: any = null;

  recentHistory: any[] = [];

  // NEW: Track which song is actively playing for the Pause button
  currentPlayingSongId: number | null = null;
  isCurrentlyPlaying: boolean = false;

  ngOnInit() {
    const storedName = this.authService.getUserName();
    this.userName = (storedName && storedName !== 'null') ? storedName : 'User';
    this.userRole = this.authService.getRole() || 'USER';
    
    this.fetchSongs();
    this.fetchLikedSongs();
    this.fetchRecentHistory(); 
    this.fetchAlbums(); 

    this.audioService.restoreUserSong();

    // NEW: Subscribe to Global Player state so HTML knows when to show Pause
    this.audioService.playerState$.subscribe(state => {
      if (state && state.song) {
        this.currentPlayingSongId = state.song.songId;
        this.isCurrentlyPlaying = state.isPlaying;
      } else {
        this.currentPlayingSongId = null;
        this.isCurrentlyPlaying = false;
      }
      // NEW: Tell HTML to update the UI immediately!
      this.cdr.detectChanges();
    });
  }

  fetchSongs() {
    this.songService.getAllSongs().subscribe({
      next: (data: any[]) => this.songs = data,
      error: (err: any) => console.error('Failed to load songs:', err)
    });
  }

  fetchAlbums() {
    this.albumService.getAllAlbums().subscribe({
      next: (data: any[]) => this.albums = data,
      error: (err: any) => console.error('Failed to load albums:', err)
    });
  }

  fetchLikedSongs() {
    this.songService.getLikedSongs().subscribe({
      next: (data: any[]) => this.likedSongIds = new Set(data.map((song: any) => song.songId)),
      error: (err: any) => console.error('Failed to load liked songs:', err)
    });
  }

  fetchRecentHistory() {
    this.historyService.getRecentHistory().subscribe({
      next: (data: any[]) => {
        // THE FIX: Filter out duplicate songs!
        // This keeps only the MOST RECENT play of a song, just like Spotify.
        const uniqueSongs = new Map();
        
        data.forEach(item => {
          // If we haven't seen this song ID yet, add it to our list
          if (!uniqueSongs.has(item.songId)) {
            uniqueSongs.set(item.songId, item);
          }
        });

        // Convert the unique map back into an array for the HTML to display
        this.recentHistory = Array.from(uniqueSongs.values());
      },
      error: (err: any) => console.error('Failed to load history:', err)
    });
  }

  toggleLike(event: Event, songId: number) {
    event.stopPropagation(); 
    this.songService.toggleLike(songId).subscribe({
      next: (isLiked: boolean) => isLiked ? this.likedSongIds.add(songId) : this.likedSongIds.delete(songId),
      error: (err: any) => console.error('Failed to toggle like:', err)
    });
  }

  isLiked(songId: number): boolean {
    return this.likedSongIds.has(songId);
  }

  onSearch() {
    if (this.searchQuery.trim() === '') return this.fetchSongs(); 
    this.songService.searchSongsByTitle(this.searchQuery).subscribe({
      next: (data: any[]) => this.songs = data,
      error: (err: any) => console.error('Search failed:', err)
    });
  }

  onFilterChange() {
    if (this.selectedGenre === '') return this.fetchSongs(); 
    this.songService.filterSongsByGenre(this.selectedGenre).subscribe({
      next: (data: any[]) => this.songs = data,
      error: (err: any) => console.error('Filter failed:', err)
    });
  }

  clearFilters() {
    this.searchQuery = '';
    this.selectedGenre = '';
    this.fetchSongs();
  }

  goToAlbum(albumId: number) {
    this.router.navigate(['/album', albumId]);
  }

  playSong(song: any) {
    let currentQueue = this.songs;
    
    if (!this.songs.some(s => s.songId === song.songId)) {
      currentQueue = this.recentHistory;
    }

    // THE FIX: Look up the full song data so we guarantee we have the MP3 URL!
    const fullSong = this.songs.find(s => s.songId == song.songId) || song;

    // THE FIX: Check if the song is already playing, and if it has finished!
    const audioEl = document.querySelector('audio');
    const isSameSong = this.currentPlayingSongId === song.songId;
    const isFinished = audioEl ? audioEl.ended : false;

    // Map the data using 'fullSong' so the URL is never null
    const songToPlay = {
      songId: fullSong.songId,
      title: fullSong.title || fullSong.songTitle,
      artistName: fullSong.artistName,
      audioFileUrl: fullSong.audioFileUrl || null,
      coverImageUrl: fullSong.coverImageUrl || null 
    };

    // Ensure the entire queue also has their full URLs!
    const mappedQueue = currentQueue.map(s => {
      const fullQueueSong = this.songs.find(fs => fs.songId == s.songId) || s;
      return {
        songId: fullQueueSong.songId,
        title: fullQueueSong.title || fullQueueSong.songTitle,
        artistName: fullQueueSong.artistName,
        audioFileUrl: fullQueueSong.audioFileUrl || null,
        coverImageUrl: fullQueueSong.coverImageUrl || null 
      };
    });

    // 1. Send to the Audio Service (it will handle the actual pausing/resuming)
    this.audioService.playSong(songToPlay, mappedQueue);

    // 2. SMART LOGIC: Only increase count if it's a NEW song, or a RESTART of a finished song
    if (!isSameSong || isFinished) {
      
      if (song.playCount !== undefined) {
        this.songService.incrementPlayCount(song.songId).subscribe({
          next: () => song.playCount = (song.playCount || 0) + 1,
          error: (err: any) => console.error('Failed to update play count:', err)
        });
      }

      if (song.songId) {
        this.historyService.logPlay(song.songId).subscribe({
          next: () => this.fetchRecentHistory(), 
          error: (err: any) => console.error('Failed to log history:', err)
        });
      }
      
    }
  }

  openPlaylistModal(event: Event, song: any) {
    event.stopPropagation(); 
    this.selectedSongForPlaylist = song;
    this.playlistService.getMyPlaylists().subscribe({
      next: (data: any[]) => {
        this.myPlaylists = data;
        this.showPlaylistModal = true;
      },
      error: (err: any) => console.error('Failed to load playlists', err)
    });
  }

  closePlaylistModal() {
    this.showPlaylistModal = false;
    this.selectedSongForPlaylist = null;
  }

  addToPlaylist(playlistId: number) {
    if (!this.selectedSongForPlaylist) return;
    this.playlistService.addSongToPlaylist(playlistId, this.selectedSongForPlaylist.songId).subscribe({
      next: (response: string) => {
        alert(response || "Song added to playlist!");
        this.closePlaylistModal();
      },
      error: (err: any) => alert("Failed to add song. It might already be in this playlist.")
    });
  }

  getCoverImageUrl(fileName: string | null): string {
    return fileName ? `${environment.apiUrl}/songs/image/${fileName}` : 'assets/default-cover.jpg';
  }

  onLogout() {
    this.audioService.clearSong();
    this.authService.logout();       
    this.router.navigate(['/login']); 
  }
}