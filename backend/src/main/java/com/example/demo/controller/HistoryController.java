package com.example.demo.controller;

import com.example.demo.dto.HistoryDTO;
import com.example.demo.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    // Accepts an optional playlistId as a query parameter
    // Example: POST http://localhost:8080/api/history/log?songId=5&playlistId=1
    @PostMapping("/log")
    public ResponseEntity<String> logPlay(@RequestParam Long songId,
                                          @RequestParam(required = false) Long playlistId,
                                          Authentication authentication) {
        String email = authentication.getName();
        historyService.logSongPlay(email, songId, playlistId);
        return ResponseEntity.ok("{\"message\": \"Song play logged successfully.\"}");
    }

    // Get recent 50 history
    @GetMapping("/recent")
    public ResponseEntity<List<HistoryDTO>> getRecentHistory(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(historyService.getRecentHistory(email));
    }

    // Get complete history
    @GetMapping("/all")
    public ResponseEntity<List<HistoryDTO>> getCompleteHistory(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(historyService.getCompleteHistory(email));
    }

    // Get Playlist-Specific History
    @GetMapping("/playlist/{playlistId}")
    public ResponseEntity<List<HistoryDTO>> getPlaylistHistory(@PathVariable Long playlistId,
                                                               Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(historyService.getPlaylistHistory(email, playlistId));
    }

    // Clear History
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearHistory(Authentication authentication) {
        String email = authentication.getName();
        historyService.clearHistory(email);
        return ResponseEntity.ok("{\"message\": \"Listening history cleared successfully.\"}");
    }

    // Total Listening Time
    @GetMapping("/stats/time")
    public ResponseEntity<String> getTotalTime(Authentication authentication) {
        String email = authentication.getName();
        String timeStats = historyService.getTotalListeningTime(email);
        return ResponseEntity.ok("{\"totalListeningTime\": \"" + timeStats + "\"}");
    }
}