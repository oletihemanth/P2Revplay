package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// 1. Tell JUnit we are using Mockito to fake our dependencies
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // 2. @Mock creates "fake" versions of our database and security tools
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    // 3. @InjectMocks creates the real AuthService but injects the fakes into it!
    @InjectMocks
    private AuthService authService;

    // --- TEST 1: Successful Registration ---
    @Test
    void registerUser_Success() {
        // Arrange (Setup the fake data)
        RegisterRequest request = new RegisterRequest();
        request.setName("Test Artist");
        request.setEmail("test@test.com");
        request.setPassword("password123");
        request.setRole("ARTIST");

        // Tell our fake repository to pretend the email doesn't exist yet
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Tell our fake encoder to pretend it hashed the password
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        // Act (Run the actual method)
        String response = authService.registerUser(request);

        // Assert (Verify the results)
        assertEquals("User registered successfully!", response);
        // Verify that userRepository.save() was called exactly 1 time
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- TEST 2: Failed Registration (Email Already Exists) ---
    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@test.com");

        // Tell our fake repository to pretend the email IS already in the database
        when(userRepository.existsByEmail("duplicate@test.com")).thenReturn(true);

        // Act & Assert
        // We expect a RuntimeException to be thrown here
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(request);
        });

        assertEquals("Error: Email is already in use!", exception.getMessage());
        // Verify that save() was NEVER called because the code should have stopped
        verify(userRepository, never()).save(any(User.class));
    }

    // --- TEST 3: Successful Login ---
    @Test
    void loginUser_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        User fakeUser = new User();
        fakeUser.setEmail("test@test.com");
        fakeUser.setRole("ARTIST");
        fakeUser.setName("Test Artist");

        // We don't need to mock authenticationManager.authenticate() if it succeeds, it just returns.

        // Pretend the database found the user
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(fakeUser));
        // Pretend the JWT util generated a token
        when(jwtUtil.generateToken("test@test.com", "ROLE_ARTIST")).thenReturn("fake-jwt-token");

        // Act
        Map<String, String> response = authService.loginUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.get("token"));
        assertEquals("ARTIST", response.get("role"));
        assertEquals("Test Artist", response.get("name"));
    }
}