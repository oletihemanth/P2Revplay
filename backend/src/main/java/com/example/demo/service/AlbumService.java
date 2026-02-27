package com.example.demo.service;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.dto.SongDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Artist;
import com.example.demo.entity.Song;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final FileStorageService fileStorageService; // NEW: Inject FileStorageService

    public AlbumService(AlbumRepository albumRepository,
                        ArtistRepository artistRepository,
                        SongRepository songRepository,
                        FileStorageService fileStorageService) { // Updated constructor
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.fileStorageService = fileStorageService;
    }

    // --- Get Single Album with Tracklist ---
    public AlbumDTO getAlbumById(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        return mapToDTOWithSongs(album);
    }

    // --- Create Album (Now supports Image Upload) ---
    public AlbumDTO createAlbum(String email, String title, String description, Integer releaseYear, MultipartFile coverImage) {
        Artist artist = artistRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album = new Album();
        album.setTitle(title);
        album.setDescription(description);
        album.setArtist(artist);

        if (releaseYear != null) {
            album.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
        }

        // Save the uploaded image
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageName = fileStorageService.storeFile(coverImage);
            album.setCoverImageUrl(coverImageName);
        }

        albumRepository.save(album);
        return mapToDTO(album);
    }

    // --- View My Albums ---
    public List<AlbumDTO> getMyAlbums(String email) {
        Artist artist = artistRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        return albumRepository.findByArtist_ArtistId(artist.getArtistId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // --- Update Album (Now supports Image Upload) ---
    public AlbumDTO updateAlbum(Long albumId, String title, String description, Integer releaseYear, MultipartFile coverImage) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        album.setTitle(title);
        album.setDescription(description);

        if (releaseYear != null) {
            album.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
        }

        // Save the new image, optionally you could delete the old one here
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageName = fileStorageService.storeFile(coverImage);
            album.setCoverImageUrl(coverImageName);
        }

        albumRepository.save(album);
        return mapToDTO(album);
    }

    // --- Delete Album ---
    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            throw new RuntimeException("Cannot delete album. Songs exist.");
        }

        // Delete the image file from storage if it exists
        if (album.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(album.getCoverImageUrl());
        }

        albumRepository.delete(album);
    }

    // --- Add Song to Album ---
    @Transactional
    public String addSongToAlbum(String email, Long albumId, Long songId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!album.getArtist().getUser().getEmail().equals(email) || !song.getArtist().getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only modify your own albums and songs!");
        }

        song.setAlbum(album);
        songRepository.save(song);
        return "Song successfully added to the album!";
    }

    // --- Remove Song from Album ---
    @Transactional
    public String removeSongFromAlbum(String email, Long albumId, Long songId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!album.getArtist().getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only modify your own albums!");
        }

        if (song.getAlbum() != null && song.getAlbum().getAlbumId().equals(albumId)) {
            song.setAlbum(null);
            songRepository.save(song);
        }

        return "Song successfully removed from the album!";
    }

    // --- Get All Albums (For Public Home Feed) ---
    public List<AlbumDTO> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Helper Mappers ---
    private AlbumDTO mapToDTO(Album album) {
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getAlbumId());
        dto.setTitle(album.getTitle());
        dto.setDescription(album.getDescription());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverImageUrl(album.getCoverImageUrl());

        if (album.getReleaseDate() != null) {
            dto.setReleaseYear(album.getReleaseDate().getYear());
        }

        return dto;
    }

    private AlbumDTO mapToDTOWithSongs(Album album) {
        AlbumDTO dto = mapToDTO(album);
        if (album.getSongs() != null) {
            List<SongDTO> songDTOs = album.getSongs().stream().map(this::mapSongToDTO).collect(Collectors.toList());
            dto.setSongs(songDTOs);
        }
        return dto;
    }

    private SongDTO mapSongToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setDuration(song.getDuration());
        dto.setPlayCount(song.getPlayCount());
        dto.setAudioFileUrl(song.getAudioFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        if (song.getArtist() != null && song.getArtist().getUser() != null) {
            dto.setArtistName(song.getArtist().getUser().getName());
        }
        return dto;
    }
}