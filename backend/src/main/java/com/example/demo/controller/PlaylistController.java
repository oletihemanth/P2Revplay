package com.example.demo.controller;

import com.example.demo.dto.PlaylistRequest;
import com.example.demo.dto.PlaylistResponse;
import com.example.demo.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @RequestBody PlaylistRequest request,
            Authentication authentication
    ) {
        PlaylistResponse response = playlistService.createPlaylist(request, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists(Authentication authentication) {
        List<PlaylistResponse> playlists = playlistService.getMyPlaylists(authentication);
        return ResponseEntity.ok(playlists);
    }
}
