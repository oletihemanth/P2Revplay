package com.example.demo.service;

import com.example.demo.dto.UserProfileDTO;
import com.example.demo.dto.UserStatsDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.LikedSongRepository; // 🌟 CHANGED: Using LikedSongRepository
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final LikedSongRepository likedSongRepository; //  CHANGED

    // NEW: Injected to calculate total plays
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    //  CHANGED: Injected LikedSongRepository into the constructor
    public UserService(UserRepository userRepository, PlaylistRepository playlistRepository,
                       LikedSongRepository likedSongRepository, ArtistRepository artistRepository,
                       SongRepository songRepository) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.likedSongRepository = likedSongRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
    }

    public UserProfileDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found."));

        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());

        // NEW: Calculate total plays if the user is an artist
        if ("ARTIST".equals(user.getRole())) {
            artistRepository.findByUser(user).ifPresent(artist -> {
                long plays = songRepository.findByArtist(artist).stream()
                        .mapToLong(song -> song.getPlayCount() != null ? song.getPlayCount() : 0L)
                        .sum();
                dto.setTotalPlays(plays);
            });
        }

        return dto;
    }

    // NEW: Update Display Name Logic
    @Transactional
    public void updateDisplayName(String email, String newName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(newName);
        userRepository.save(user);
    }

    // Calculate User Statistics
    public UserStatsDTO getUserStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long playlistCount = playlistRepository.countByUser(user);

        //  CHANGED: Now correctly counting from the LikedSong table where the hearts are saved!
        long favoritesCount = likedSongRepository.findByUser(user).size();

        UserStatsDTO stats = new UserStatsDTO();
        stats.setTotalPlaylists(playlistCount);
        stats.setFavoriteSongsCount(favoritesCount);

        // Temporarily set to 0. We will connect this to real data when we build the History module!
        stats.setTotalListeningTimeMinutes(0L);

        return stats;
    }
}