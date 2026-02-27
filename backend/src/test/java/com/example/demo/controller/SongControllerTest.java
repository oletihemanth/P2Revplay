package com.example.demo.controller;

import com.example.demo.dto.SongDTO;
import com.example.demo.service.SongService;
import com.example.demo.service.FileStorageService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SongController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters for the unit test
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SongService songService;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // --- TEST 1: View Public Songs ---
    @Test
    void getAllSongs_ReturnsListOfSongs() throws Exception {
        SongDTO song = new SongDTO();
        song.setTitle("Test Song");
        song.setGenre("Pop");

        // Tell the fake service what to return
        when(songService.getAllSongs()).thenReturn(List.of(song));

        // Simulate a GET request
        mockMvc.perform(get("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Song"));
    }

    // --- TEST 2: Upload a Song (The Complex File Test) ---
    @Test
    void uploadSong_ReturnsOkStatus() throws Exception {
        // 1. Create fake files for the upload
        // FIX: Changed "audioFile" to "file" to match what the Controller specifically asked for!
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "fake-audio-content".getBytes());
        MockMultipartFile coverImage = new MockMultipartFile(
                "coverImage", "cover.jpg", "image/jpeg", "fake-image-content".getBytes());

        // 2. Setup the expected response
        SongDTO savedSong = new SongDTO();
        savedSong.setTitle("New Upload");

        when(songService.uploadSong(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(savedSong);

        TestingAuthenticationToken mockAuth = new TestingAuthenticationToken("artist@test.com", "pass", "ROLE_ARTIST");

        // 3. Simulate a POST request with MULTIPART form data
        mockMvc.perform(multipart("/api/songs")
                        .file(audioFile)
                        .file(coverImage)
                        .param("title", "New Upload")
                        .param("genre", "Rock")
                        .param("duration", "200")
                        .param("visibility", "PUBLIC")
                        .param("albumId", "1")
                        .param("description", "Test")
                        .param("artistId", "1")
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Upload"));
    }
}