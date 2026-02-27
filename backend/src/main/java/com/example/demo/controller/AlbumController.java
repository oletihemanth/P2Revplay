package com.example.demo.controller;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.service.AlbumService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumDTO>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumById(albumId));
    }

    // UPDATED: Consumes Multipart Data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDTO> createAlbum(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "releaseYear", required = false) Integer releaseYear,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        String email = authentication.getName();
        return ResponseEntity.ok(albumService.createAlbum(email, title, description, releaseYear, coverImage));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AlbumDTO>> getMyAlbums(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(albumService.getMyAlbums(email));
    }

    // UPDATED: Consumes Multipart Data
    @PutMapping(value = "/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDTO> updateAlbum(
            @PathVariable Long albumId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "releaseYear", required = false) Integer releaseYear,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, title, description, releaseYear, coverImage));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.ok("{\"message\": \"Album deleted successfully\"}");
    }

    @PostMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<String> addSongToAlbum(Authentication authentication,
                                                 @PathVariable Long albumId,
                                                 @PathVariable Long songId) {
        String message = albumService.addSongToAlbum(authentication.getName(), albumId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromAlbum(Authentication authentication,
                                                      @PathVariable Long albumId,
                                                      @PathVariable Long songId) {
        String message = albumService.removeSongFromAlbum(authentication.getName(), albumId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }
}