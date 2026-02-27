package com.example.demo.service;

import com.example.demo.dto.PlaylistRequest;
import com.example.demo.dto.PlaylistResponse;
import com.example.demo.entity.Playlist;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.entity.Favorite;
import com.example.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurationServiceTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private PlaylistRepository playlistRepository;
    @Mock private UserRepository userRepository;
    @Mock private SongRepository songRepository;

    @InjectMocks
    private CurationService curationService;

    @Test
    void createPlaylist_Success() {
        // Arrange
        User user = new User();
        user.setEmail("user@test.com");
        user.setName("Test User");

        PlaylistRequest request = new PlaylistRequest();
        request.setName("Workout Vibes");
        request.setDescription("Gym playlist");
        request.setPrivacy("PUBLIC");

        Playlist savedPlaylist = new Playlist();
        savedPlaylist.setPlaylistId(1L);
        savedPlaylist.setName("Workout Vibes");
        savedPlaylist.setPrivacy("PUBLIC");
        savedPlaylist.setUser(user);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(savedPlaylist);

        // Act: FIX -> Pass the 5 parameters your new method expects in the EXACT order!
        // (email MUST be first based on your CurationService logic)
        PlaylistResponse response = curationService.createPlaylist(
                "user@test.com",           // 1. Email (FIRST!)
                request.getName(),         // 2. Title
                request.getDescription(),  // 3. Description
                request.getPrivacy(),      // 4. Privacy
                null                       // 5. Image File
        );

        // Assert
        assertNotNull(response);
        assertEquals("Workout Vibes", response.getName());
        assertEquals("Test User", response.getCreatorName());
    }

    @Test
    void toggleFavorite_AddsNewFavorite() {
        // Arrange
        User user = new User();
        user.setEmail("user@test.com");
        Song song = new Song();
        song.setSongId(10L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(songRepository.findById(10L)).thenReturn(Optional.of(song));
        // Simulate that the song is NOT currently favorited
        when(favoriteRepository.findByUserAndSong(user, song)).thenReturn(Optional.empty());

        // Act
        String result = curationService.toggleFavorite("user@test.com", 10L);

        // Assert
        assertEquals("Song added to favorites!", result);
        verify(favoriteRepository, times(1)).save(any(Favorite.class)); // Verifies it saved a new favorite
    }
}