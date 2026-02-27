import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router'; // Added Router
import { FormsModule } from '@angular/forms'; // Added FormsModule for the Edit Modal
import { Album } from '../../../core/services/album/album';
import { Song } from '../../../core/services/song/song';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-manage-album',
  standalone: true,
  imports: [CommonModule, FormsModule], // Added FormsModule
  templateUrl: './manage-album.html',
  styleUrl: './manage-album.css'
})
export class ManageAlbum implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private albumService = inject(Album);
  private songService = inject(Song);
  private location = inject(Location);

  albumId: number = 0;
  album: any = null;
  
  albumSongs: any[] = []; 
  availableSongs: any[] = []; 
  isLoading: boolean = true;

  //  NEW: State for Edit Modal
  showEditModal: boolean = false;
  editAlbumData = { title: '', releaseYear: new Date().getFullYear(), description: '', coverImageUrl: '' };
  selectedEditImage: File | null = null; //  NEW: Track updated file

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.albumId = +params['id'];
      if (this.albumId) {
        this.loadAlbumData();
      }
    });
  }

  loadAlbumData() {
    this.isLoading = true;
    this.albumService.getAlbumById(this.albumId).subscribe({
      next: (albumData: any) => {
        this.album = albumData;
        this.albumSongs = albumData.songs || [];
        this.loadAvailableSongs();
      },
      error: (err: any) => {
        console.error('Failed to load album:', err);
        this.isLoading = false;
      }
    });
  }

  loadAvailableSongs() {
    this.songService.getMyUploadedSongs().subscribe({
      next: (allMySongs: any[]) => {
        const albumSongIds = this.albumSongs.map((s: any) => s.songId);
        this.availableSongs = allMySongs.filter(song => !albumSongIds.includes(song.songId));
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Failed to load your songs:', err);
        this.isLoading = false;
      }
    });
  }

  addSong(songId: number) {
    this.albumService.addSongToAlbum(this.albumId, songId).subscribe({
      next: () => this.loadAlbumData(), 
      error: (err: any) => alert('Failed to add song to album.')
    });
  }

  removeSong(songId: number) {
    this.albumService.removeSongFromAlbum(this.albumId, songId).subscribe({
      next: () => this.loadAlbumData(), 
      error: (err: any) => alert('Failed to remove song from album.')
    });
  }

  // NEW: Open Edit Modal and pre-fill data
  openEditModal() {
    this.editAlbumData = {
      title: this.album.title,
      releaseYear: this.album.releaseYear || new Date().getFullYear(),
      description: this.album.description || '',
      coverImageUrl: this.album.coverImageUrl || ''
    };
    this.selectedEditImage = null; // Reset file input
    this.showEditModal = true;
  }

  closeEditModal() {
    this.showEditModal = false;
  }

  //  NEW: Capture file update
  onEditFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.selectedEditImage = event.target.files[0];
    }
  }

  // NEW: Save Album Updates
  saveAlbumChanges() {
    if (!this.editAlbumData.title.trim()) {
      alert('Album title is required!');
      return;
    }

    //  PACK IT INTO FORMDATA
    const formData = new FormData();
    formData.append('title', this.editAlbumData.title);
    if (this.editAlbumData.releaseYear) {
      formData.append('releaseYear', this.editAlbumData.releaseYear.toString());
    }
    if (this.editAlbumData.description) {
      formData.append('description', this.editAlbumData.description);
    }
    if (this.selectedEditImage) {
      formData.append('coverImage', this.selectedEditImage);
    }

    this.albumService.updateAlbum(this.albumId, formData).subscribe({
      next: () => {
        this.loadAlbumData(); // Reload UI
        this.closeEditModal();
      },
      error: (err: any) => alert('Failed to update album.')
    });
  }

  // NEW: Delete Album (With Safety Check)
  deleteAlbum() {
    if (this.albumSongs.length > 0) {
      alert('⚠️ You must remove all songs from this album before deleting it!');
      return;
    }

    if (confirm('Are you sure you want to permanently delete this album? This cannot be undone.')) {
      this.albumService.deleteAlbum(this.albumId).subscribe({
        next: () => {
          alert('Album deleted successfully!');
          this.router.navigate(['/my-albums']); // Send them back to the dashboard
        },
        error: (err: any) => alert('Failed to delete album.')
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

  goBack() {
    this.router.navigate(['/my-albums']);
  }
}