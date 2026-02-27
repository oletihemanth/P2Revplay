import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Home } from './features/home/home';
import { UploadSong } from './features/music/upload-song/upload-song'; 
import { Favorites } from './features/favorites/favorites'; 
import { MyMusic } from './features/music/my-music/my-music'; 
import { Playlists } from './features/playlists/playlists/playlists';
import { PlaylistDetail } from './features/playlists/playlist-detail/playlist-detail'; 
import { DiscoverPlaylists } from './features/playlists/discover-playlists/discover-playlists'; 
import { Profile } from './features/profile/profile/profile'; 
import { ArtistAnalytics } from './features/artist-analytics/artist-analytics'; 
import { MyAlbums } from './features/albums/my-albums/my-albums'; 
import { ManageAlbum } from './features/albums/manage-album/manage-album';
import { AlbumDetail } from './features/albums/album-detail/album-detail'; // NEW: Import Public Album Detail

import { authGuard } from './core/guards/auth/auth-guard';  
import { artistGuard } from './core/guards/artist/artist-guard';  

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  
  // Protected by authGuard (must be logged in)
  { path: 'home', component: Home, canActivate: [authGuard] }, 
  { path: 'favorites', component: Favorites, canActivate: [authGuard] }, 
  { path: 'playlists', component: Playlists, canActivate: [authGuard] },
  
  // Protected Discover route
  { path: 'discover', component: DiscoverPlaylists, canActivate: [authGuard] }, 
  
  { path: 'playlists/:id', component: PlaylistDetail, canActivate: [authGuard] }, 
  
  // NEW: Public Album Route (For Listeners and Artists)
  { path: 'album/:id', component: AlbumDetail, canActivate: [authGuard] },
  
  // Protected Profile route
  { path: 'profile', component: Profile, canActivate: [authGuard] }, 
  
  // Protected by artistGuard (must be logged in AND an ARTIST)
  { path: 'upload', component: UploadSong, canActivate: [artistGuard] },
  { path: 'my-music', component: MyMusic, canActivate: [artistGuard] },
  { path: 'analytics', component: ArtistAnalytics, canActivate: [artistGuard] }, 
  { path: 'my-albums', component: MyAlbums, canActivate: [artistGuard] }, 
  { path: 'manage-album/:id', component: ManageAlbum, canActivate: [artistGuard] }, 
  
  { path: '', redirectTo: '/login', pathMatch: 'full' } 
];