package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.CurationService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/curation")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;

    // Toggle favorite for a song
    @PostMapping("/favorite/{songId}")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long songId, Authentication authentication) {
        String message = curationService.toggleFavorite(songId, authentication);
        return ResponseEntity.ok(message);
    }
}
