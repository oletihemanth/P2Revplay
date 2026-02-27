package com.example.demo.controller;

import com.example.demo.dto.UserProfileDTO;
import com.example.demo.dto.UserStatsDTO;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint: GET http://localhost:8080/api/users/me
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserProfileDTO profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    //  NEW Endpoint: PUT http://localhost:8080/api/users/me/name
    // Allows the user to update their display name
    @PutMapping("/me/name")
    public ResponseEntity<String> updateDisplayName(Authentication authentication, @RequestParam String name) {
        userService.updateDisplayName(authentication.getName(), name);
        return ResponseEntity.ok("{\"message\": \"Display name updated successfully.\"}");
    }

    // Endpoint: GET http://localhost:8080/api/users/me/stats
    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(Authentication authentication) {
        String email = authentication.getName();
        UserStatsDTO stats = userService.getUserStats(email);
        return ResponseEntity.ok(stats);
    }
}