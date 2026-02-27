package com.example.demo.service;

import com.example.demo.dto.PlaylistResponse;
import com.example.demo.dto.SongDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurationService {

    private final FavoriteRepository favoriteRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final FollowedPlaylistRepository followedPlaylistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final FileStorageService fileStorageService; //  NEW: Inject FileStorageService

    public CurationService(FavoriteRepository favoriteRepository, PlaylistRepository playlistRepository,
                           PlaylistSongRepository playlistSongRepository, FollowedPlaylistRepository followedPlaylistRepository,
                           UserRepository userRepository, SongRepository songRepository, FileStorageService fileStorageService) {
        this.favoriteRepository = favoriteRepository;
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.followedPlaylistRepository = followedPlaylistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.fileStorageService = fileStorageService;
    }

    // --- 1. FAVORITES LOGIC ---
    @Transactional
    public String toggleFavorite(String email, Long songId) {
        User user = getUser(email);
        Song song = getSong(songId);
        Optional<Favorite> existing = favoriteRepository.findByUserAndSong(user, song);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return "Song removed from favorites.";
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setSong(song);
            favoriteRepository.save(favorite);
            return "Song added to favorites!";
        }
    }

    public List<SongDTO> getMyFavorites(String email) {
        User user = getUser(email);
        return favoriteRepository.findByUser(user).stream()
                .map(favorite -> mapSongToDTO(favorite.getSong()))
                .collect(Collectors.toList());
    }

    public long getFavoritesCount(String email) {
        User user = getUser(email);
        return favoriteRepository.countByUser(user);
    }

    // --- 2. PLAYLIST CRUD LOGIC ---
    public PlaylistResponse getPlaylistById(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        PlaylistResponse response = mapToResponse(playlist);

        List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistOrderBySongOrderAsc(playlist);

        List<SongDTO> songDTOs = playlistSongs.stream()
                .map(ps -> mapSongToDTO(ps.getSong()))
                .collect(Collectors.toList());

        response.setSongs(songDTOs);
        return response;
    }

    //  UPDATED: Handle MultipartFile for Create
    @Transactional
    public PlaylistResponse createPlaylist(String email, String name, String description, String privacy, MultipartFile coverImage) {
        Playlist playlist = new Playlist();
        playlist.setUser(getUser(email));
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPrivacy(privacy != null ? privacy : "PUBLIC");

        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageName = fileStorageService.storeFile(coverImage);
            playlist.setCoverImageUrl(coverImageName);
        }

        return mapToResponse(playlistRepository.save(playlist));
    }

    //  UPDATED: Handle MultipartFile for Update
    @Transactional
    public PlaylistResponse updatePlaylist(String email, Long playlistId, String name, String description, String privacy, MultipartFile coverImage) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPrivacy(privacy);

        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageName = fileStorageService.storeFile(coverImage);
            playlist.setCoverImageUrl(coverImageName);
        }

        return mapToResponse(playlistRepository.save(playlist));
    }

    //  UPDATED: Delete image if it exists
    @Transactional
    public String deletePlaylist(String email, Long playlistId) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);

        if (playlist.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(playlist.getCoverImageUrl());
        }

        playlistRepository.delete(playlist);
        return "Playlist deleted successfully.";
    }

    public List<PlaylistResponse> getMyPlaylists(String email) {
        return playlistRepository.findByUser(getUser(email))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<PlaylistResponse> getPublicPlaylists() {
        return playlistRepository.findByPrivacy("PUBLIC")
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- 3. PLAYLIST SONGS LOGIC ---
    @Transactional
    public String addSongToPlaylist(String email, Long playlistId, Long songId) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);
        Song song = getSong(songId);

        if (playlistSongRepository.findByPlaylistAndSong(playlist, song).isPresent()) {
            return "Song is already in this playlist.";
        }

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(song);
        ps.setSongOrder(1);

        playlistSongRepository.save(ps);
        return "Song added to playlist!";
    }

    @Transactional
    public String removeSongFromPlaylist(String email, Long playlistId, Long songId) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);
        Song song = getSong(songId);

        PlaylistSong ps = playlistSongRepository.findByPlaylistAndSong(playlist, song)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found in this playlist"));

        playlistSongRepository.delete(ps);
        return "Song removed from playlist.";
    }

    // --- 4. FOLLOW / UNFOLLOW PLAYLIST LOGIC ---
    @Transactional
    public String toggleFollowPlaylist(String email, Long playlistId) {
        User user = getUser(email);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if ("PRIVATE".equals(playlist.getPrivacy()) && !playlist.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Cannot follow a private playlist.");
        }

        Optional<FollowedPlaylist> existing = followedPlaylistRepository.findByUserAndPlaylist(user, playlist);

        if (existing.isPresent()) {
            followedPlaylistRepository.delete(existing.get());
            return "Unfollowed playlist.";
        } else {
            FollowedPlaylist follow = new FollowedPlaylist();
            follow.setUser(user);
            follow.setPlaylist(playlist);
            followedPlaylistRepository.save(follow);
            return "Successfully followed playlist!";
        }
    }

    // --- HELPER METHODS ---
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Song getSong(Long songId) {
        return songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));
    }

    private Playlist getPlaylistAsOwner(String email, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
        if (!playlist.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized: You do not own this playlist");
        }
        return playlist;
    }

    private PlaylistResponse mapToResponse(Playlist playlist) {
        PlaylistResponse response = new PlaylistResponse();
        response.setPlaylistId(playlist.getPlaylistId());
        response.setName(playlist.getName());
        response.setDescription(playlist.getDescription());
        response.setPrivacy(playlist.getPrivacy());
        response.setCreatorName(playlist.getUser().getName());
        response.setCoverImageUrl(playlist.getCoverImageUrl()); //  NEW
        return response;
    }

    private SongDTO mapSongToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setDuration(song.getDuration());
        dto.setPlayCount(song.getPlayCount());
        dto.setAudioFileUrl(song.getAudioFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        if (song.getArtist() != null && song.getArtist().getUser() != null) {
            dto.setArtistName(song.getArtist().getUser().getName());
        }
        return dto;
    }
}