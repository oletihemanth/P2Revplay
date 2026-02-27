import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Playlist } from '../../../core/services/playlist/playlist'; 

@Component({
  selector: 'app-discover-playlists',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './discover-playlists.html',
  styleUrl: './discover-playlists.css'
})
export class DiscoverPlaylists implements OnInit {
  private playlistService = inject(Playlist);

  publicPlaylists: any[] = [];
  filteredPlaylists: any[] = [];
  searchQuery: string = '';

  ngOnInit() {
    this.fetchPublicPlaylists();
  }

  fetchPublicPlaylists() {
    this.playlistService.getPublicPlaylists().subscribe({
      next: (data: any[]) => {
        this.publicPlaylists = data;
        this.filteredPlaylists = data; // Initialize the view with all data
      },
      error: (err: any) => console.error('Failed to load public playlists:', err)
    });
  }

  onSearch() {
    if (!this.searchQuery.trim()) {
      this.filteredPlaylists = this.publicPlaylists;
      return;
    }
    const query = this.searchQuery.toLowerCase();
    this.filteredPlaylists = this.publicPlaylists.filter(p => 
      p.name.toLowerCase().includes(query) || 
      (p.creatorName && p.creatorName.toLowerCase().includes(query))
    );
  }
}