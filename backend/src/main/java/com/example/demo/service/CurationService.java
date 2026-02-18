package com.example.demo.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurationService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public String toggleFavorite(Long songId, Authentication authentication) {

        // 1️⃣ Get email from JWT
        String email = authentication.getName();

        // 2️⃣ Fetch User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3️⃣ Fetch Song
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // 4️⃣ Check if already favorited
        Optional<Favorite> existingFavorite =
                favoriteRepository.findByUserAndSong(user, song);

        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return "Song removed from favorites.";
        }

        // 5️⃣ Create new Favorite (no need for FavoriteId object)
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setSong(song);

        favoriteRepository.save(favorite);

        return "Song added to favorites!";
    }
}
