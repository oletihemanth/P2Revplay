package com.example.demo.controller;

import com.example.demo.dto.SongDTO;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.SongService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;
    private final FileStorageService fileStorageService;

    public SongController(SongService songService, FileStorageService fileStorageService) {
        this.songService = songService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long songId) {
        return ResponseEntity.ok(songService.getSongById(songId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String title) {
        return ResponseEntity.ok(songService.searchSongsByTitle(title));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SongDTO>> filterSongs(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false, name = "artist") String artistName,
            @RequestParam(required = false, name = "album") String albumName,
            @RequestParam(required = false, name = "year") Integer releaseYear) {
        return ResponseEntity.ok(songService.filterSongs(genre, artistName, albumName, releaseYear));
    }

    @GetMapping("/my-songs")
    public ResponseEntity<List<SongDTO>> getMyUploadedSongs(Authentication authentication) {
        return ResponseEntity.ok(songService.getMyUploadedSongs(authentication.getName()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongDTO> uploadSong(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam(value = "duration", required = false, defaultValue = "0") Integer duration,
            @RequestParam(value = "visibility", defaultValue = "PUBLIC") String visibility,
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam("file") MultipartFile audioFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {

        return ResponseEntity.ok(songService.uploadSong(
                authentication.getName(), title, genre, duration, visibility, albumId, audioFile, coverImage));
    }

    @PutMapping("/{songId}")
    public ResponseEntity<SongDTO> updateSong(
            Authentication authentication,
            @PathVariable Long songId,
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("visibility") String visibility) {

        return ResponseEntity.ok(songService.updateSong(authentication.getName(), songId, title, genre, visibility));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<String> deleteSong(Authentication authentication, @PathVariable Long songId) {
        songService.deleteSong(authentication.getName(), songId);
        return ResponseEntity.ok("{\"message\": \"Song deleted successfully.\"}");
    }

    @GetMapping("/play/{fileName:.+}")
    public ResponseEntity<Resource> playAudio(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = "image/jpeg";
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception e) {
            // Ignore error and use default
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @PostMapping("/{songId}/like")
    public ResponseEntity<Boolean> toggleLikeSong(Authentication authentication, @PathVariable Long songId) {
        return ResponseEntity.ok(songService.toggleLikeSong(authentication.getName(), songId));
    }

    @GetMapping("/{songId}/like-status")
    public ResponseEntity<Boolean> getLikeStatus(Authentication authentication, @PathVariable Long songId) {
        return ResponseEntity.ok(songService.isSongLikedByUser(authentication.getName(), songId));
    }

    @GetMapping("/liked")
    public ResponseEntity<List<SongDTO>> getLikedSongs(Authentication authentication) {
        return ResponseEntity.ok(songService.getLikedSongs(authentication.getName()));
    }

    // --- NEW: PLAY COUNT ENDPOINT ---
    @PostMapping("/{songId}/increment-play")
    public ResponseEntity<String> incrementPlayCount(@PathVariable Long songId) {
        songService.incrementPlayCount(songId);
        return ResponseEntity.ok("{\"message\": \"Play count updated successfully\"}");
    }
}