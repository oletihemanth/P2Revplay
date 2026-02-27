import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Album } from '../../../core/services/album/album';
import { AudioService } from '../../../core/services/audio/audio'; // NEW: Global Player
import { Song } from '../../../core/services/song/song'; // NEW: For Play Counts
import { History } from '../../../core/services/history/history'; // NEW: For Recently Played
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-album-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './album-detail.html',
  styleUrl: './album-detail.css'
})
export class AlbumDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private albumService = inject(Album);
  private location = inject(Location);
  
  // NEW: Injecting the services needed for playback
  private audioService = inject(AudioService);
  private songService = inject(Song);
  private historyService = inject(History);

  albumId: number = 0;
  album: any = null;
  albumSongs: any[] = [];
  isLoading: boolean = true;

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.albumId = +params['id'];
      if (this.albumId) {
        this.loadAlbum();
      }
    });
  }

  loadAlbum() {
    this.isLoading = true;
    this.albumService.getAlbumById(this.albumId).subscribe({
      next: (data: any) => {
        this.album = data;
        this.albumSongs = data.songs || [];
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error fetching album', err);
        this.isLoading = false;
      }
    });
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return '';
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }

  onImageError(event: any) {
    event.target.style.display = 'none';
  }

  goBack() {
    this.location.back();
  }

  // NEW: The Magic Playback Function!
  playSong(song: any) {
    // 1. Map the ENTIRE album into a queue format your player understands
    const mappedQueue = this.albumSongs.map(s => ({
      songId: s.songId,
      title: s.title || s.songTitle,
      artistName: s.artistName || 'Unknown Artist',
      audioFileUrl: s.audioFileUrl || null,
      // If the song doesn't have its own cover, use the Album's cover!
      coverImageUrl: s.coverImageUrl || this.album.coverImageUrl || null 
    }));

    // 2. Find the exact song they clicked within our newly mapped queue
    const songToPlay = mappedQueue.find(s => s.songId === song.songId);

    if (songToPlay) {
      // 3. Send it to the Global Player!
      this.audioService.playSong(songToPlay, mappedQueue);

      // 4. Update the play count in the database
      this.songService.incrementPlayCount(song.songId).subscribe({
        next: () => song.playCount = (song.playCount || 0) + 1,
        error: (err: any) => console.error('Failed to update play count:', err)
      });

      // 5. Add it to their "Recently Played" history
      this.historyService.logPlay(song.songId).subscribe({
        error: (err: any) => console.error('Failed to log history:', err)
      });
    }
  }

  // NEW: Plays the full album starting from track 1
  playFullAlbum() {
    if (this.albumSongs.length > 0) {
      this.playSong(this.albumSongs[0]); 
    }
  }
}