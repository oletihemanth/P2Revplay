package com.example.demo.repository;

import com.example.demo.entity.LikedSong;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, Long> {

    // Find a specific like (useful for un-liking a song)
    Optional<LikedSong> findByUserAndSong(User user, Song song);

    // Check if a user has already liked a song (so we can color the heart red)
    boolean existsByUserAndSong(User user, Song song);

    // Get all the songs a user has liked (for the Favorites page)
    List<LikedSong> findByUser(User user);
}