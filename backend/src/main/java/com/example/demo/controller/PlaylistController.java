package com.example.demo.controller;

import com.example.demo.dto.PlaylistResponse;
import com.example.demo.service.CurationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final CurationService curationService;

    public PlaylistController(CurationService curationService) {
        this.curationService = curationService;
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable Long playlistId) {
        return ResponseEntity.ok(curationService.getPlaylistById(playlistId));
    }

    //  UPDATED: Consumes Multipart Data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistResponse> createPlaylist(
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "privacy", defaultValue = "PUBLIC") String privacy,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        return ResponseEntity.ok(curationService.createPlaylist(authentication.getName(), name, description, privacy, coverImage));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists(Authentication authentication) {
        return ResponseEntity.ok(curationService.getMyPlaylists(authentication.getName()));
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlaylistResponse>> getPublicPlaylists() {
        return ResponseEntity.ok(curationService.getPublicPlaylists());
    }

    //  UPDATED: Consumes Multipart Data
    @PutMapping(value = "/{playlistId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistResponse> updatePlaylist(
            Authentication authentication,
            @PathVariable Long playlistId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "privacy", defaultValue = "PUBLIC") String privacy,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        return ResponseEntity.ok(curationService.updatePlaylist(authentication.getName(), playlistId, name, description, privacy, coverImage));
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> deletePlaylist(Authentication authentication, @PathVariable Long playlistId) {
        String message = curationService.deletePlaylist(authentication.getName(), playlistId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> addSongToPlaylist(Authentication authentication, @PathVariable Long playlistId, @PathVariable Long songId) {
        String message = curationService.addSongToPlaylist(authentication.getName(), playlistId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromPlaylist(Authentication authentication, @PathVariable Long playlistId, @PathVariable Long songId) {
        String message = curationService.removeSongFromPlaylist(authentication.getName(), playlistId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    @PostMapping("/{playlistId}/follow")
    public ResponseEntity<String> toggleFollowPlaylist(Authentication authentication, @PathVariable Long playlistId) {
        String message = curationService.toggleFollowPlaylist(authentication.getName(), playlistId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }
}