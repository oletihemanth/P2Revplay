package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
// --- NEW IMPORTS ---
import com.example.demo.security.JwtUtil;
import com.example.demo.security.CustomUserDetailsService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypasses security filters just for this unit test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // --- THE FIX: Mock the missing security dependencies so context loads! ---
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_ReturnsOkStatus() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Web Artist");
        request.setEmail("web@test.com");
        request.setPassword("pass123");

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn("User registered successfully!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void login_ReturnsTokenAndOkStatus() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("web@test.com");
        request.setPassword("pass123");

        Map<String, String> fakeResponse = new HashMap<>();
        fakeResponse.put("token", "fake-jwt-token");
        fakeResponse.put("role", "ARTIST");

        when(authService.loginUser(any(LoginRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("ARTIST"));
    }
}