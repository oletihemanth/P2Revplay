package com.example.demo.service;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritesService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public FavoritesService(FavoriteRepository favoriteRepository,
                            UserRepository userRepository,
                            SongRepository songRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    @Transactional
    public String toggleFavorite(Long songId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        return favoriteRepository.findByUserAndSong(user, song)
                .map(fav -> {
                    favoriteRepository.delete(fav); // Unlike
                    return "Song removed from favorites.";
                })
                .orElseGet(() -> {
                    favoriteRepository.save(new Favorite(user, song)); // Like
                    return "Song added to favorites!";
                });
    }

    public List<Song> getUserFavorites(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findByUser(user)
                .stream()
                .map(Favorite::getSong)
                .toList();
    }
}
