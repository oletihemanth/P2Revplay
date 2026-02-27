package com.example.demo.dto;

import java.time.LocalDateTime;

public class HistoryDTO {
    private Long historyId;

    // ADDED: songId so the frontend can easily play the song again
    private Long songId;

    private String songTitle;
    private String artistName;
    private String coverImageUrl;
    private LocalDateTime playedAt;

    // --- NEW: Playlist context ---
    private Long playlistId;
    private String playlistName;

    // --- Getters and Setters ---
    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    // ADDED: Getters and Setters for songId
    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }

    public String getSongTitle() { return songTitle; }
    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }

    public Long getPlaylistId() { return playlistId; }
    public void setPlaylistId(Long playlistId) { this.playlistId = playlistId; }

    public String getPlaylistName() { return playlistName; }
    public void setPlaylistName(String playlistName) { this.playlistName = playlistName; }
}