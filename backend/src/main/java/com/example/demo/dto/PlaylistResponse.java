package com.example.demo.dto;

import java.util.List;

public class PlaylistResponse {
    private Long playlistId;
    private String name;
    private String description;
    private String privacy;
    private String creatorName;
    private String coverImageUrl; //  ADDED: Required to send image to frontend
    private List<SongDTO> songs;

    // --- Getters and Setters ---
    public Long getPlaylistId() { return playlistId; }
    public void setPlaylistId(Long playlistId) { this.playlistId = playlistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrivacy() { return privacy; }
    public void setPrivacy(String privacy) { this.privacy = privacy; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getCoverImageUrl() { return coverImageUrl; } //  ADDED Getter
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; } // 🌟 ADDED Setter

    public List<SongDTO> getSongs() { return songs; }
    public void setSongs(List<SongDTO> songs) { this.songs = songs; }
}