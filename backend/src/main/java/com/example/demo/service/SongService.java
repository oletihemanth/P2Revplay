package com.example.demo.service;

import com.example.demo.dto.SongDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Artist;
import com.example.demo.entity.LikedSong;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.LikedSongRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final FileStorageService fileStorageService;
    private final LikedSongRepository likedSongRepository;

    public SongService(SongRepository songRepository, UserRepository userRepository,
                       ArtistRepository artistRepository, AlbumRepository albumRepository,
                       FileStorageService fileStorageService, LikedSongRepository likedSongRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.fileStorageService = fileStorageService;
        this.likedSongRepository = likedSongRepository;
    }

    public List<SongDTO> getAllSongs() {
        return songRepository.findByVisibility("PUBLIC").stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<SongDTO> searchSongsByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCaseAndVisibility(title, "PUBLIC")
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<SongDTO> filterSongs(String genre, String artistName, String albumName, Integer releaseYear) {
        return songRepository.filterSongs(genre, artistName, albumName, releaseYear)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public SongDTO getSongById(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        return mapToDTO(song);
    }

    public List<SongDTO> getMyUploadedSongs(String email) {
        Artist artist = getArtistByEmail(email);
        return songRepository.findByArtist(artist)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleLikeSong(String email, Long songId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        Optional<LikedSong> existingLike = likedSongRepository.findByUserAndSong(user, song);
        if (existingLike.isPresent()) {
            likedSongRepository.delete(existingLike.get());
            return false;
        } else {
            likedSongRepository.save(new LikedSong(user, song));
            return true;
        }
    }

    public boolean isSongLikedByUser(String email, Long songId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        return likedSongRepository.existsByUserAndSong(user, song);
    }

    public List<SongDTO> getLikedSongs(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return likedSongRepository.findByUser(user).stream()
                .map(LikedSong::getSong)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SongDTO uploadSong(String email, String title, String genre, Integer duration,
                              String visibility, Long albumId, MultipartFile audioFile, MultipartFile coverImage) {
        Artist artist = getArtistByEmail(email);
        String audioFileName = fileStorageService.storeFile(audioFile);

        String coverImageName = coverImage != null ? fileStorageService.storeFile(coverImage) : null;

        if (audioFileName == null) {
            throw new RuntimeException("Audio file is required!");
        }

        Song song = new Song();
        song.setTitle(title);
        song.setGenre(genre);

        song.setDuration(duration != null ? duration : 0);
        song.setVisibility(visibility != null ? visibility : "PUBLIC");
        song.setArtist(artist);
        song.setAudioFileUrl(audioFileName);
        song.setCoverImageUrl(coverImageName);

        if (albumId != null) {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
            if (!album.getArtist().getArtistId().equals(artist.getArtistId())) {
                throw new RuntimeException("You do not own this album!");
            }
            song.setAlbum(album);
        }

        return mapToDTO(songRepository.save(song));
    }

    @Transactional
    public SongDTO updateSong(String email, Long songId, String title, String genre, String visibility) {
        Artist artist = getArtistByEmail(email);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!song.getArtist().getArtistId().equals(artist.getArtistId())) {
            throw new RuntimeException("You can only update your own songs!");
        }

        song.setTitle(title);
        song.setGenre(genre);
        song.setVisibility(visibility);

        return mapToDTO(songRepository.save(song));
    }

    @Transactional
    public void deleteSong(String email, Long songId) {
        Artist artist = getArtistByEmail(email);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!song.getArtist().getArtistId().equals(artist.getArtistId())) {
            throw new RuntimeException("You can only delete your own songs!");
        }

        fileStorageService.deleteFile(song.getAudioFileUrl());
        if(song.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(song.getCoverImageUrl());
        }
        songRepository.delete(song);
    }

    // --- NEW: PLAY COUNT TRACKER ---
    @Transactional
    public void incrementPlayCount(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // Fix: Since playCount is a Long object, we safely handle nulls and add 1L.
        Long currentCount = song.getPlayCount() != null ? song.getPlayCount() : 0L;
        song.setPlayCount(currentCount + 1L);

        songRepository.save(song);
    }

    private Artist getArtistByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return artistRepository.findByUser(user).orElseGet(() -> {
            System.out.println("Auto-creating blank Artist profile for: " + email);
            Artist newArtist = new Artist();
            newArtist.setUser(user);
            return artistRepository.save(newArtist);
        });
    }

    private SongDTO mapToDTO(Song song) {
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
        if (song.getAlbum() != null) {
            dto.setAlbumName(song.getAlbum().getTitle());
        }
        return dto;
    }
}