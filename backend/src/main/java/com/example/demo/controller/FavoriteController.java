package com.example.demo.controller;

import com.example.demo.entity.Song;
import com.example.demo.service.FavoritesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {   // <-- Singular name

    private final FavoritesService favoritesService;

    public FavoriteController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    // Toggle favorite (Like / Unlike)
    @PostMapping("/{songId}")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long songId, Authentication authentication) {
        String email = authentication.getName();
        String message = favoritesService.toggleFavorite(songId, email);
        return ResponseEntity.ok(message);
    }

    // Get all favorites for logged-in user
    @GetMapping
    public ResponseEntity<List<Song>> getUserFavorites(Authentication authentication) {
        String email = authentication.getName();
        List<Song> favorites = favoritesService.getUserFavorites(email);
        return ResponseEntity.ok(favorites);
    }
}
