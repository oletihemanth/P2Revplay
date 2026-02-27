package com.example.demo.repository;

import com.example.demo.entity.Artist;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.FavoriteId;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    Optional<Favorite> findByUserAndSong(User user, Song song);
    long countByUser(User user);
    List<Favorite> findByUser(User user);

    // --- NEW: For Artist Analytics ---
    List<Favorite> findBySong_Artist(Artist artist);
}