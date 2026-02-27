import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Playlist } from '../../../core/services/playlist/playlist';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-playlists',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './playlists.html', 
  styleUrl: './playlists.css'      
})
export class Playlists implements OnInit {
  private playlistService = inject(Playlist);

  myPlaylists: any[] = [];
  showCreateModal: boolean = false;
  
  isEditing: boolean = false;
  editingPlaylistId: number | null = null;
  selectedCoverImage: File | null = null; //  NEW: Track selected file
  
  newPlaylist = {
    name: '',
    description: '',
    privacy: 'PUBLIC'
  };

  ngOnInit() {
    this.fetchPlaylists();
  }

  fetchPlaylists() {
    this.playlistService.getMyPlaylists().subscribe({
      next: (data: any[]) => {
        this.myPlaylists = data;
      },
      error: (err: any) => console.error('Failed to load playlists:', err)
    });
  }

  openCreateModal() {
    this.isEditing = false;
    this.editingPlaylistId = null;
    this.selectedCoverImage = null; // Reset image
    this.newPlaylist = { name: '', description: '', privacy: 'PUBLIC' }; 
    this.showCreateModal = true;
  }

  openEditModal(event: Event, playlist: any) {
    event.stopPropagation(); 
    this.isEditing = true;
    this.editingPlaylistId = playlist.playlistId;
    this.selectedCoverImage = null; // Reset image
    this.newPlaylist = { 
      name: playlist.name, 
      description: playlist.description || '', 
      privacy: playlist.privacy 
    };
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  //  NEW: Capture file event
  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.selectedCoverImage = event.target.files[0];
    }
  }

  savePlaylist() {
    if (!this.newPlaylist.name.trim()) {
      alert("Playlist name cannot be empty!");
      return;
    }

    //  PACK IT INTO FORMDATA
    const formData = new FormData();
    formData.append('name', this.newPlaylist.name);
    if (this.newPlaylist.description) formData.append('description', this.newPlaylist.description);
    formData.append('privacy', this.newPlaylist.privacy);
    if (this.selectedCoverImage) formData.append('coverImage', this.selectedCoverImage);

    if (this.isEditing && this.editingPlaylistId) {
      this.playlistService.updatePlaylist(this.editingPlaylistId, formData).subscribe({
        next: () => {
          this.fetchPlaylists(); 
          this.closeCreateModal(); 
        },
        error: (err: any) => console.error('Failed to update playlist:', err)
      });
    } else {
      this.playlistService.createPlaylist(formData).subscribe({
        next: () => {
          this.fetchPlaylists(); 
          this.closeCreateModal(); 
        },
        error: (err: any) => console.error('Failed to create playlist:', err)
      });
    }
  }

  deletePlaylist(event: Event, playlistId: number) {
    event.stopPropagation(); 
    if (confirm('Are you sure you want to delete this playlist?')) {
      this.playlistService.deletePlaylist(playlistId).subscribe({
        next: () => {
          this.myPlaylists = this.myPlaylists.filter(p => p.playlistId !== playlistId);
        },
        error: (err: any) => console.error('Failed to delete playlist:', err)
      });
    }
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return ''; 
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }

  onImageError(event: any) {
    event.target.style.display = 'none';
  }
}