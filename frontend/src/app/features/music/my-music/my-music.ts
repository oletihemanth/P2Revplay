import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Song } from '../../../core/services/song/song'; 
import { environment } from '../../../../environments/environment'; 

@Component({
  selector: 'app-my-music',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-music.html', //  Update to .component.html if needed by your CLI
  styleUrl: './my-music.css'      //  Update to .component.css if needed
})
export class MyMusic implements OnInit {
  private songService = inject(Song);

  mySongs: any[] = [];
  isEditing: boolean = false;
  
  // Temporary object to hold the data while the artist is typing in the edit form
  editSongData: any = { songId: 0, title: '', genre: '', visibility: 'PUBLIC' };

  ngOnInit() {
    this.fetchMySongs();
  }

  fetchMySongs() {
    this.songService.getMyUploadedSongs().subscribe({
      next: (data: any[]) => this.mySongs = data,
      error: (err: any) => console.error('Failed to load my songs:', err)
    });
  }

  // 🗑️ Delete a Song
  deleteSong(songId: number) {
    if (confirm('Are you sure you want to delete this track forever?')) {
      this.songService.deleteSong(songId).subscribe({
        next: () => {
          alert('Song deleted successfully!');
          // Instantly remove it from the screen without refreshing
          this.mySongs = this.mySongs.filter(song => song.songId !== songId);
        },
        error: (err: any) => console.error('Failed to delete song:', err)
      });
    }
  }

  // Open the Edit Modal
  startEdit(song: any) {
    this.isEditing = true;
    this.editSongData = {
      songId: song.songId,
      title: song.title,
      genre: song.genre,
      visibility: song.visibility || 'PUBLIC'
    };
  }

  cancelEdit() {
    this.isEditing = false;
  }

  //  Save the Edit
  saveEdit() {
    this.songService.updateSong(
      this.editSongData.songId,
      this.editSongData.title,
      this.editSongData.genre,
      this.editSongData.visibility
    ).subscribe({
      next: () => {
        alert('Song updated successfully!');
        this.isEditing = false;
        this.fetchMySongs(); // Reload the list to show the new title/genre
      },
      error: (err: any) => console.error('Failed to update song:', err)
    });
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return 'assets/default-cover.jpg';
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }
}