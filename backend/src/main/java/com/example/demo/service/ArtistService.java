package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Artist;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.History;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.HistoryRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final HistoryRepository historyRepository;
    private final FavoriteRepository favoriteRepository;

    public ArtistService(ArtistRepository artistRepository, UserRepository userRepository,
                         SongRepository songRepository, HistoryRepository historyRepository,
                         FavoriteRepository favoriteRepository) {
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.historyRepository = historyRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional
    public String setupOrUpdateArtistProfile(String email, ArtistProfileRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"ARTIST".equals(user.getRole())) {
            user.setRole("ARTIST");
            userRepository.save(user);
        }

        Artist artist = artistRepository.findByUser(user).orElseGet(Artist::new);
        artist.setUser(user);
        artist.setBio(request.getBio());
        artist.setGenre(request.getGenre());
        artist.setSocialLinks(request.getSocialLinks());
        artist.setBannerImageUrl(request.getBannerImageUrl());

        artistRepository.save(artist);
        return "Artist profile successfully updated!";
    }

    public ArtistPublicResponse getPublicProfile(Long artistId) {
        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        return mapToPublicResponse(artist);
    }

    public ArtistPublicResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Artist artist = artistRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Profile not setup"));
        return mapToPublicResponse(artist);
    }

    // --- MAIN DASHBOARD DATA ---
    public ArtistStatsResponse getArtistAnalytics(String email) {
        Artist artist = getArtist(email);
        List<Song> artistSongs = songRepository.findByArtist(artist);
        List<Favorite> favorites = favoriteRepository.findBySong_Artist(artist);

        ArtistStatsResponse response = new ArtistStatsResponse();
        response.setArtistName(artist.getUser().getName());
        response.setGenre(artist.getGenre());
        response.setTotalFavorites(favorites.size());

        if (artistSongs != null && !artistSongs.isEmpty()) {
            response.setTotalSongsUploaded(artistSongs.size());
            response.setTotalAllTimePlays(artistSongs.stream().mapToLong(s -> s.getPlayCount() != null ? s.getPlayCount() : 0).sum());

            List<SongPerformanceDTO> topSongs = artistSongs.stream()
                    .sorted((s1, s2) -> Long.compare(s2.getPlayCount() != null ? s2.getPlayCount() : 0, s1.getPlayCount() != null ? s1.getPlayCount() : 0))
                    .limit(10)
                    .map(song -> {
                        SongPerformanceDTO dto = new SongPerformanceDTO();
                        dto.setSongId(song.getSongId());
                        dto.setTitle(song.getTitle());
                        dto.setPlayCount(song.getPlayCount() != null ? song.getPlayCount() : 0L);
                        return dto;
                    }).collect(Collectors.toList());
            response.setTopSongs(topSongs);
        } else {
            response.setTotalSongsUploaded(0);
            response.setTotalAllTimePlays(0L);
        }
        return response;
    }

    // --- NEW: WHO FAVORITED MY SONGS ---
    public List<UserProfileDTO> getUsersWhoFavorited(String email) {
        Artist artist = getArtist(email);
        List<Favorite> favorites = favoriteRepository.findBySong_Artist(artist);

        return favorites.stream()
                .map(Favorite::getUser)
                .distinct() // Prevent duplicates if a user liked 2 songs
                .map(this::mapToUserProfileDTO)
                .collect(Collectors.toList());
    }

    // --- NEW: TOP LISTENERS ---
    public List<TopListenerDTO> getTopListeners(String email) {
        Artist artist = getArtist(email);
        List<History> history = historyRepository.findBySong_Artist(artist);

        // Group history records by the User, and count them
        Map<User, Long> userPlayCounts = history.stream()
                .collect(Collectors.groupingBy(History::getUser, Collectors.counting()));

        // Sort by play count and return top 10
        return userPlayCounts.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .map(e -> new TopListenerDTO(e.getKey().getName(), e.getKey().getProfilePictureUrl(), e.getValue()))
                .collect(Collectors.toList());
    }

    // --- NEW: DAILY LISTENING TRENDS ---
    public List<TrendDTO> getListeningTrends(String email) {
        Artist artist = getArtist(email);
        List<History> history = historyRepository.findBySong_Artist(artist);

        // Group history records by the exact Date they were played
        Map<LocalDate, Long> dailyPlays = history.stream()
                .collect(Collectors.groupingBy(h -> h.getPlayedAt().toLocalDate(), Collectors.counting()));

        // Sort chronologically
        return dailyPlays.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new TrendDTO(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
    }

    // --- HELPERS ---
    private Artist getArtist(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return artistRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Artist profile not found."));
    }

    private ArtistPublicResponse mapToPublicResponse(Artist artist) {
        ArtistPublicResponse response = new ArtistPublicResponse();
        response.setArtistId(artist.getArtistId());
        response.setName(artist.getUser().getName());
        response.setProfilePictureUrl(artist.getUser().getProfilePictureUrl());
        response.setBio(artist.getBio());
        response.setGenre(artist.getGenre());
        response.setBannerImageUrl(artist.getBannerImageUrl());
        response.setSocialLinks(artist.getSocialLinks());
        return response;
    }

    private UserProfileDTO mapToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        return dto;
    }
}