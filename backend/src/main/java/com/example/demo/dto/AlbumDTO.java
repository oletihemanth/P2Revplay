package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class AlbumDTO {

    private Long albumId;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private String coverImageUrl;
    private List<SongDTO> songs;

    //  NEW: Added to perfectly match the Angular frontend!
    private Integer releaseYear;

    // --- Getters and Setters ---
    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public List<SongDTO> getSongs() { return songs; }
    public void setSongs(List<SongDTO> songs) { this.songs = songs; }

    //  NEW: Getters and Setters for Release Year
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
}