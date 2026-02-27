package com.example.demo.service;

import com.example.demo.dto.HistoryDTO;
import com.example.demo.entity.History;
import com.example.demo.entity.Playlist;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.HistoryRepository;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;

    public HistoryService(HistoryRepository historyRepository, UserRepository userRepository,
                          SongRepository songRepository, PlaylistRepository playlistRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
    }

    @Transactional
    public void logSongPlay(String email, Long songId, Long playlistId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // Increment the total play count for the song
        song.setPlayCount((song.getPlayCount() != null ? song.getPlayCount() : 0) + 1);
        songRepository.save(song);

        // Create the history record using our convenience constructor
        History history = new History(user, song);

        // Link playlist if provided
        if (playlistId != null) {
            Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
            history.setPlaylist(playlist);
        }

        historyRepository.save(history);
    }

    public List<HistoryDTO> getCompleteHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository.findByUser_UserIdOrderByPlayedAtDesc(user.getUserId())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<HistoryDTO> getRecentHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository.findTop50ByUser_UserIdOrderByPlayedAtDesc(user.getUserId())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<HistoryDTO> getPlaylistHistory(String email, Long playlistId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository.findByUser_UserIdAndPlaylist_PlaylistIdOrderByPlayedAtDesc(user.getUserId(), playlistId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void clearHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        historyRepository.deleteByUser(user);
    }

    public String getTotalListeningTime(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long totalSeconds = historyRepository.calculateTotalListeningTimeInSeconds(user);

        // Handle null case safely
        if (totalSeconds == null || totalSeconds == 0) return "0 minutes";

        long minutes = totalSeconds / 60;
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " hours, " + remainingMinutes + " minutes";
        }
    }

    // DTO Mapper
    private HistoryDTO mapToDTO(History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setHistoryId(history.getHistoryId());

        // Safety checks in case a song was deleted
        if (history.getSong() != null) {
            dto.setSongId(history.getSong().getSongId()); // Good practice to include the ID
            dto.setSongTitle(history.getSong().getTitle());
            dto.setCoverImageUrl(history.getSong().getCoverImageUrl());

            if (history.getSong().getArtist() != null && history.getSong().getArtist().getUser() != null) {
                dto.setArtistName(history.getSong().getArtist().getUser().getName());
            }
        }

        dto.setPlayedAt(history.getPlayedAt());

        if (history.getPlaylist() != null) {
            dto.setPlaylistId(history.getPlaylist().getPlaylistId());
            dto.setPlaylistName(history.getPlaylist().getName());
        }

        return dto;
    }
}