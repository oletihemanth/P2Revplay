package com.example.demo.dto;

public class PlaylistDTO {
    private Long playlistId;
    private String name;
    private String description;
    private String privacy;
    private String creatorName;
    private int songCount; // Summary info instead of full song list

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
    public int getSongCount() { return songCount; }
    public void setSongCount(int songCount) { this.songCount = songCount; }
}