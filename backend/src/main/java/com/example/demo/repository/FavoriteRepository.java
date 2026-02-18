package com.example.demo.repository;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.FavoriteId;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    // Find a favorite by user and song (for toggle)
    Optional<Favorite> findByUserAndSong(User user, Song song);

    // Find all favorites for a user
    List<Favorite> findByUser(User user);
}
