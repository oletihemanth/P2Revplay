import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../auth/auth'; 

export interface PlayerState {
  song: any;
  autoplay: boolean;
  queue: any[];
  originalQueue: any[]; 
  currentIndex: number;
  isShuffle: boolean;
  repeatMode: 'off' | 'all' | 'one';
  isPlaying: boolean; // NEW: Track if the song is actively playing or paused
}

@Injectable({
  providedIn: 'root'
})
export class AudioService {
  private authService = inject(AuthService); 
  
  private playerStateSubject = new BehaviorSubject<PlayerState | null>(null);
  playerState$ = this.playerStateSubject.asObservable();

  private getUniqueUserId(): string {
    const token = this.authService.getToken();
    if (!token) return 'unknown_user';
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      const parsed = JSON.parse(decoded);
      return parsed.sub || parsed.email || 'unknown_user'; 
    } catch (e) {
      return 'unknown_user';
    }
  }

  playSong(song: any, queue: any[] = [], forceRestart: boolean = false) {
    const currentState = this.playerStateSubject.getValue();

    // THE SMART SENSOR LOGIC
    if (currentState && currentState.song && currentState.song.songId === song.songId) {
      const audioEl = document.querySelector('audio');
      
      if (audioEl) {
        if (forceRestart || audioEl.ended) {
          audioEl.currentTime = 0; 
          audioEl.play();
          this.playerStateSubject.next({ ...currentState, isPlaying: true }); // Sync state
        } 
        else if (audioEl.paused) {
          audioEl.play();
          this.playerStateSubject.next({ ...currentState, isPlaying: true }); // Sync state
        } 
        else {
          audioEl.pause();
          this.playerStateSubject.next({ ...currentState, isPlaying: false }); // Sync state
        }
        return; 
      }
    }

    const isShuffle = currentState ? currentState.isShuffle : false;
    const repeatMode = currentState ? currentState.repeatMode : 'off';
    
    let originalQueue = queue.length > 0 ? [...queue] : currentState?.originalQueue || [song];
    let currentQueue = [...originalQueue];

    if (isShuffle) {
      const otherSongs = currentQueue.filter(s => s.songId !== song.songId);
      for (let i = otherSongs.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [otherSongs[i], otherSongs[j]] = [otherSongs[j], otherSongs[i]];
      }
      currentQueue = [song, ...otherSongs];
    }

    let index = currentQueue.findIndex(s => s.songId === song.songId);
    if (index === -1) {
      currentQueue = [song];
      originalQueue = [song];
      index = 0;
    }

    const newState: PlayerState = { 
      song, autoplay: true, queue: currentQueue, originalQueue, currentIndex: index, isShuffle, repeatMode, 
      isPlaying: true // Set to true for brand new songs
    };
    this.playerStateSubject.next(newState);

    const userId = this.getUniqueUserId();
    if (userId !== 'unknown_user') {
      localStorage.setItem(`savedState_${userId}`, JSON.stringify(newState));
    }

    // Ensure native audio controls sync back to our state
    setTimeout(() => {
      const audioEl = document.querySelector('audio');
      if (audioEl) {
        audioEl.onpause = () => {
          const state = this.playerStateSubject.getValue();
          if (state) this.playerStateSubject.next({ ...state, isPlaying: false });
        };
        audioEl.onplay = () => {
          const state = this.playerStateSubject.getValue();
          if (state) this.playerStateSubject.next({ ...state, isPlaying: true });
        };
      }
    }, 100);
  }

  playNext(manualSkip: boolean = false) {
    const state = this.playerStateSubject.getValue();
    if (!state || !state.queue || state.queue.length === 0) return;

    if (!manualSkip && state.repeatMode === 'one') {
      this.playSong(state.song, state.originalQueue, true); 
      return;
    }

    let nextIndex = state.currentIndex + 1;
    if (nextIndex >= state.queue.length) {
      if (state.repeatMode === 'off' && !manualSkip) return;
      nextIndex = 0; 
    }
    this.playSong(state.queue[nextIndex], state.originalQueue, true); 
  }

  playPrevious() {
    const state = this.playerStateSubject.getValue();
    if (!state || !state.queue || state.queue.length === 0) return;

    let prevIndex = state.currentIndex - 1;
    if (prevIndex < 0) prevIndex = state.queue.length - 1;
    this.playSong(state.queue[prevIndex], state.originalQueue, true); 
  }

  toggleShuffle() {
    const state = this.playerStateSubject.getValue();
    if (!state) return;

    const newShuffleState = !state.isShuffle;
    let newQueue = [...state.originalQueue];
    let newIndex = 0;

    if (newShuffleState) {
      const otherSongs = newQueue.filter(s => s.songId !== state.song.songId);
      for (let i = otherSongs.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [otherSongs[i], otherSongs[j]] = [otherSongs[j], otherSongs[i]];
      }
      newQueue = [state.song, ...otherSongs];
    } else {
      newIndex = newQueue.findIndex(s => s.songId === state.song.songId);
    }

    this.playerStateSubject.next({
      ...state, isShuffle: newShuffleState, queue: newQueue, currentIndex: newIndex
    });
  }

  toggleRepeat() {
    const state = this.playerStateSubject.getValue();
    if (!state) return;

    let nextMode: 'off' | 'all' | 'one' = 'off';
    if (state.repeatMode === 'off') nextMode = 'all';
    else if (state.repeatMode === 'all') nextMode = 'one';

    this.playerStateSubject.next({ ...state, repeatMode: nextMode });
  }

  restoreUserSong() {
    if (!this.playerStateSubject.getValue()) {
      const userId = this.getUniqueUserId();
      const savedState = localStorage.getItem(`savedState_${userId}`);
      
      if (savedState) {
        try {
          const parsedState = JSON.parse(savedState);
          parsedState.autoplay = false; 
          parsedState.isPlaying = false; // Restored songs start paused
          this.playerStateSubject.next(parsedState);
        } catch (error) {
          console.error('Failed to parse saved state', error);
        }
      }
    }
  }

  clearSong() {
    this.playerStateSubject.next(null);
  }

  getAudioUrl(fileName: string): string {
    if (!fileName) return '';
    return `${environment.apiUrl}/songs/play/${fileName}`;
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return 'assets/default-cover.jpg';
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }
}