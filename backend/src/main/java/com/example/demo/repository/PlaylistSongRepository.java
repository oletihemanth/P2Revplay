package com.example.demo.repository;

import com.example.demo.entity.Playlist;
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.PlaylistSongId;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    Optional<PlaylistSong> findByPlaylistAndSong(Playlist playlist, Song song);

    // NEW: Fetch all songs for a specific playlist, sorted by the song order!
    List<PlaylistSong> findByPlaylistOrderBySongOrderAsc(Playlist playlist);
}