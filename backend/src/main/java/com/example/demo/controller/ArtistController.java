package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    // Create/Update your own profile
    @PostMapping("/profile")
    public ResponseEntity<String> updateProfile(Authentication authentication, @RequestBody ArtistProfileRequest request) {
        String message = artistService.setupOrUpdateArtistProfile(authentication.getName(), request);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    // View your own profile details
    @GetMapping("/profile/me")
    public ResponseEntity<ArtistPublicResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(artistService.getMyProfile(authentication.getName()));
    }

    // View ANY artist's public profile by their ID
    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistPublicResponse> getArtistProfile(@PathVariable Long artistId) {
        return ResponseEntity.ok(artistService.getPublicProfile(artistId));
    }

    // --- ANALYTICS ENDPOINTS ---

    // 1. Dashboard (Total plays, songs, top songs, total favorites)
    @GetMapping("/stats")
    public ResponseEntity<ArtistStatsResponse> getAnalytics(Authentication authentication) {
        return ResponseEntity.ok(artistService.getArtistAnalytics(authentication.getName()));
    }

    // 2. NEW: Users who favorited my songs
    @GetMapping("/analytics/favorited-by")
    public ResponseEntity<List<UserProfileDTO>> getUsersWhoFavorited(Authentication authentication) {
        return ResponseEntity.ok(artistService.getUsersWhoFavorited(authentication.getName()));
    }

    // 3. NEW: Top Listeners (Fans who played my music the most)
    @GetMapping("/analytics/top-listeners")
    public ResponseEntity<List<TopListenerDTO>> getTopListeners(Authentication authentication) {
        return ResponseEntity.ok(artistService.getTopListeners(authentication.getName()));
    }

    // 4. NEW: Daily Listening Trends
    @GetMapping("/analytics/trends")
    public ResponseEntity<List<TrendDTO>> getListeningTrends(Authentication authentication) {
        return ResponseEntity.ok(artistService.getListeningTrends(authentication.getName()));
    }
}