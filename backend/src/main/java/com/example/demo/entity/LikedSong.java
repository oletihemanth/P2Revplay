package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "liked_songs")
public class LikedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who liked the song
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The song that was liked
    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public LikedSong() {}

    public LikedSong(User user, Song song) {
        this.user = user;
        this.song = song;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Song getSong() { return song; }
    public void setSong(Song song) { this.song = song; }
}