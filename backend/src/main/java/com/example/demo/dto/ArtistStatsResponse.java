package com.example.demo.dto;
import java.util.List;

public class ArtistStatsResponse {
    private String artistName;
    private String genre;
    private int totalSongsUploaded;
    private long totalAllTimePlays;
    private long totalFavorites; // --- NEW ---
    private List<SongPerformanceDTO> topSongs;

    // Getters and Setters
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getTotalSongsUploaded() { return totalSongsUploaded; }
    public void setTotalSongsUploaded(int totalSongsUploaded) { this.totalSongsUploaded = totalSongsUploaded; }
    public long getTotalAllTimePlays() { return totalAllTimePlays; }
    public void setTotalAllTimePlays(long totalAllTimePlays) { this.totalAllTimePlays = totalAllTimePlays; }
    public long getTotalFavorites() { return totalFavorites; }
    public void setTotalFavorites(long totalFavorites) { this.totalFavorites = totalFavorites; }
    public List<SongPerformanceDTO> getTopSongs() { return topSongs; }
    public void setTopSongs(List<SongPerformanceDTO> topSongs) { this.topSongs = topSongs; }
}