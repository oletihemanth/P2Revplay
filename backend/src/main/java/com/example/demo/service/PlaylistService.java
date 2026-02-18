package com.example.demo.service;

import com.example.demo.dto.PlaylistRequest;
import com.example.demo.dto.PlaylistResponse;
import com.example.demo.entity.Playlist;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    public PlaylistResponse createPlaylist(PlaylistRequest dto, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist playlist = new Playlist();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());
        playlist.setPrivacy(dto.getPrivacy() != null ? dto.getPrivacy() : "PUBLIC");
        playlist.setUser(user);

        Playlist saved = playlistRepository.save(playlist);

        return new PlaylistResponse(
                saved.getPlaylistId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrivacy(),
                saved.getCoverImageUrl()
        );
    }

    public List<PlaylistResponse> getMyPlaylists(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return playlistRepository.findByUser(user)
                .stream()
                .map(p -> new PlaylistResponse(
                        p.getPlaylistId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrivacy(),
                        p.getCoverImageUrl()
                ))
                .collect(Collectors.toList());
    }
}
