import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AudioService } from '../../../core/services/audio/audio';

@Component({
  selector: 'app-global-player',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './global-player.html',
  styleUrl: './global-player.css'
})
export class GlobalPlayer {
  public audioService = inject(AudioService);

  isPlaying: boolean = false;
  currentTime: number = 0;
  duration: number = 0;
  volume: number = 1; // 1 = 100% volume
  previousVolume: number = 1;

  formatTime(seconds: number): string {
    if (!seconds || isNaN(seconds)) return '0:00';
    const min = Math.floor(seconds / 60);
    const sec = Math.floor(seconds % 60);
    return `${min}:${sec < 10 ? '0' : ''}${sec}`;
  }

  togglePlay(audio: HTMLAudioElement) {
    if (!audio) return;
    if (audio.paused) audio.play();
    else audio.pause();
  }

  onTimeUpdate(audio: HTMLAudioElement) {
    if (!audio) return;
    this.currentTime = audio.currentTime;
    this.duration = audio.duration || 0;
  }

  seekTo(event: Event, audio: HTMLAudioElement) {
    if (!audio) return;
    const input = event.target as HTMLInputElement;
    audio.currentTime = Number(input.value);
    this.currentTime = audio.currentTime;
  }

  // NEW: Volume Controls
  setVolume(event: Event, audio: HTMLAudioElement) {
    if (!audio) return;
    const input = event.target as HTMLInputElement;
    this.volume = Number(input.value);
    audio.volume = this.volume;
  }

  toggleMute(audio: HTMLAudioElement) {
    if (!audio) return;
    if (this.volume > 0) {
      this.previousVolume = this.volume;
      this.volume = 0;
    } else {
      this.volume = this.previousVolume || 1;
    }
    audio.volume = this.volume;
  }

  // Wrapper for manual next button click
  skipNext() {
    this.audioService.playNext(true); // true = manual skip
  }

  onAudioPlay() {
    this.isPlaying = true;
  }

  onAudioPause() {
    this.isPlaying = false;
  }
}