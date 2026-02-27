package com.example.demo.controller;

import com.example.demo.dto.SongDTO;
import com.example.demo.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final CurationService curationService;

    public FavoriteController(CurationService curationService) {
        this.curationService = curationService;
    }

    // Existing: Get all my favorite songs
    @GetMapping
    public ResponseEntity<List<SongDTO>> getMyFavorites(Authentication authentication) {
        return ResponseEntity.ok(curationService.getMyFavorites(authentication.getName()));
    }

    // --- NEW: Get Total Favorites Count ---
    @GetMapping("/count")
    public ResponseEntity<String> getFavoritesCount(Authentication authentication) {
        long count = curationService.getFavoritesCount(authentication.getName());
        return ResponseEntity.ok("{\"count\": " + count + "}");
    }

    // Existing: Toggle Favorite
    @PostMapping("/{songId}")
    public ResponseEntity<String> toggleFavorite(Authentication authentication, @PathVariable Long songId) {
        String message = curationService.toggleFavorite(authentication.getName(), songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }
}