import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Album } from '../../../core/services/album/album';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-my-albums',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-albums.html',
  styleUrl: './my-albums.css'
})
export class MyAlbums implements OnInit {
  private albumService = inject(Album);
  private router = inject(Router);

  albums: any[] = [];
  isLoading: boolean = true;

  // Modal State
  showCreateModal: boolean = false;
  newAlbum = { title: '', releaseYear: new Date().getFullYear() };
  selectedCoverImage: File | null = null; // NEW: Track selected file

  ngOnInit() {
    this.fetchMyAlbums();
  }

  fetchMyAlbums() {
    this.isLoading = true;
    this.albumService.getMyAlbums().subscribe({
      next: (data: any[]) => {
        this.albums = data;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Failed to load albums', err);
        this.isLoading = false;
      }
    });
  }

  openCreateModal() {
    this.newAlbum = { title: '', releaseYear: new Date().getFullYear() };
    this.selectedCoverImage = null; // Reset file input
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  // NEW: Capture file event
  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.selectedCoverImage = event.target.files[0];
    }
  }

  createAlbum() {
    if (!this.newAlbum.title.trim()) {
      alert('Album title is required!');
      return;
    }

    // PACK IT INTO FORMDATA
    const formData = new FormData();
    formData.append('title', this.newAlbum.title);
    if (this.newAlbum.releaseYear) {
      formData.append('releaseYear', this.newAlbum.releaseYear.toString());
    }
    if (this.selectedCoverImage) {
      formData.append('coverImage', this.selectedCoverImage);
    }

    this.albumService.createAlbum(formData).subscribe({
      next: (createdAlbum: any) => {
        this.albums.push(createdAlbum); 
        this.closeCreateModal();
      },
      error: (err: any) => {
        console.error('Failed to create album', err);
        alert('Could not create album. Please try again.');
      }
    });
  }

  //  BULLETPROOF FIX: Pass the whole album and check for both id types
  goToAlbumDetails(album: any) {
    const id = album.albumId || album.id; // Checks both common backend names
    
    if (id) {
      this.router.navigate(['/manage-album', id]);
    } else {
      console.error('Missing ID! Here is the album object:', album);
      alert('Oops! The backend did not send an ID for this album.');
    }
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return ''; // Return empty so the HTML placeholder kicks in
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }

  //  NEW: Hides the image tag completely if the URL is broken
  onImageError(event: any) {
    event.target.style.display = 'none';
  }
}