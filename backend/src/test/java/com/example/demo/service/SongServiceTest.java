package com.example.demo.service;

import com.example.demo.dto.SongDTO;
import com.example.demo.entity.Artist;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    // 1. Mock all the dependencies the SongService needs
    @Mock
    private SongRepository songRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private FileStorageService fileStorageService;

    // 2. Inject those mocks into the real service
    @InjectMocks
    private SongService songService;

    // --- TEST 1: Get All Public Songs ---
    @Test
    void getAllSongs_ReturnsOnlyPublicSongs() {
        // Arrange
        Song publicSong = new Song();
        publicSong.setSongId(1L);
        publicSong.setTitle("Public Hit");
        publicSong.setVisibility("PUBLIC");

        // Tell the fake repo to return our fake song when asked for PUBLIC songs
        when(songRepository.findByVisibility("PUBLIC")).thenReturn(List.of(publicSong));

        // Act
        List<SongDTO> result = songService.getAllSongs();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Public Hit", result.get(0).getTitle());
        verify(songRepository, times(1)).findByVisibility("PUBLIC");
    }

    // --- TEST 2: Get Song By ID (Success) ---
    @Test
    void getSongById_Success() {
        // Arrange
        User fakeUser = new User();
        fakeUser.setName("Rockstar");

        Artist fakeArtist = new Artist();
        fakeArtist.setUser(fakeUser);

        Song fakeSong = new Song();
        fakeSong.setSongId(100L);
        fakeSong.setTitle("My Awesome Track");
        fakeSong.setArtist(fakeArtist);

        when(songRepository.findById(100L)).thenReturn(Optional.of(fakeSong));

        // Act
        SongDTO result = songService.getSongById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getSongId());
        assertEquals("My Awesome Track", result.getTitle());
        assertEquals("Rockstar", result.getArtistName()); // Verifies the DTO mapping worked!
    }

    // --- TEST 3: Get Song By ID (Not Found) ---
    @Test
    void getSongById_NotFound_ThrowsException() {
        // Arrange
        when(songRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            songService.getSongById(999L);
        });

        assertEquals("Song not found", exception.getMessage());
    }
}