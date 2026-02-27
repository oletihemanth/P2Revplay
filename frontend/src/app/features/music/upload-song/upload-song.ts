import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Song } from '../../../core/services/song/song';  

@Component({
  selector: 'app-upload-song',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './upload-song.html',
  styleUrl: './upload-song.css'
})
export class UploadSong {
  private songService = inject(Song); 
  private router = inject(Router);

  // We now track two separate files
  selectedAudio: File | null = null;
  selectedImage: File | null = null;
  isUploading: boolean = false;

  // This method now checks if the user selected an audio file or an image file
  onFileSelected(event: any, type: string) {
    const file = event.target.files[0];
    if (file) {
      if (type === 'audio') {
        this.selectedAudio = file;
      } else if (type === 'image') {
        this.selectedImage = file;
      }
    }
  }

  onUpload(event: Event) {
    event.preventDefault();
    if (!this.selectedAudio) {
      alert('Please select an audio file first!');
      return;
    }

    this.isUploading = true;
    const form = event.target as HTMLFormElement;
    
    const formData = new FormData();
    formData.append('title', (form.elements.namedItem('title') as HTMLInputElement).value);
    formData.append('genre', (form.elements.namedItem('genre') as HTMLSelectElement).value);
    
    // Append the MP3 file
    formData.append('file', this.selectedAudio); 
    
    // NEW: If they selected a cover image, append it with the EXACT name Java expects
    if (this.selectedImage) {
      formData.append('coverImage', this.selectedImage);
    }

    console.log('Sending song and cover art to Java...');
    
    this.songService.uploadSong(formData).subscribe({
      next: (response) => {
        console.log('Upload Success:', response);
        alert('Song uploaded successfully!');
        this.isUploading = false;
        this.router.navigate(['/home']); 
      },
      error: (err) => {
        console.error('Upload Error:', err);
        alert(err.error?.message || 'Upload failed. Check the console.');
        this.isUploading = false;
      }
    });
  }
}