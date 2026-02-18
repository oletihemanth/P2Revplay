package com.example.demo.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "favorites")
@IdClass(FavoriteId.class)
public class Favorite implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    // Default constructor
    public Favorite() {}

    // Constructor for easier creation in service
    public Favorite(User user, Song song) {
        this.user = user;
        this.song = song;
    }

    // --- Getters and Setters ---
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    // --- equals and hashCode required for composite key ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorite)) return false;
        Favorite favorite = (Favorite) o;
        return Objects.equals(user, favorite.user) &&
                Objects.equals(song, favorite.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, song);
    }
}
