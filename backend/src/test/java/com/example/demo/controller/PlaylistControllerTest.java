package com.example.demo.controller;

import com.example.demo.dto.PlaylistResponse;
import com.example.demo.service.CurationService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaylistController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters for the unit test
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurationService curationService;

    // --- Mock the security dependencies so the context loads! ---
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // --- TEST 1: Create a Playlist (POST via Multipart) ---
    @Test
    void createPlaylist_ReturnsOkStatus() throws Exception {
        // Arrange: Prepare the fake response from the service
        PlaylistResponse response = new PlaylistResponse();
        response.setPlaylistId(1L);
        response.setName("My Chill Vibes");
        response.setCreatorName("Test User");

        // Create the official Spring Security fake token
        TestingAuthenticationToken mockAuth = new TestingAuthenticationToken("user@test.com", "pass", "ROLE_USER");

        when(curationService.createPlaylist(any(), any(), any(), any(), any())).thenReturn(response);

        // FIX: Use 'multipart' instead of 'post' with JSON, because your API takes file uploads!
        mockMvc.perform(multipart("/api/playlists")
                        .param("name", "My Chill Vibes")
                        .param("description", "Relaxing music")
                        .param("privacy", "PUBLIC")
                        .principal(mockAuth)) // Inject the fake user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("My Chill Vibes"))
                .andExpect(jsonPath("$.creatorName").value("Test User"));
    }

    // --- TEST 2: Get My Playlists (GET) ---
    @Test
    void getMyPlaylists_ReturnsListOfPlaylists() throws Exception {
        // Arrange
        PlaylistResponse response = new PlaylistResponse();
        response.setPlaylistId(1L);
        response.setName("My Chill Vibes");

        TestingAuthenticationToken mockAuth = new TestingAuthenticationToken("user@test.com", "pass", "ROLE_USER");

        when(curationService.getMyPlaylists("user@test.com")).thenReturn(List.of(response));

        // Act & Assert: Simulate GET request
        mockMvc.perform(get("/api/playlists/me")
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("My Chill Vibes"));
    }
}