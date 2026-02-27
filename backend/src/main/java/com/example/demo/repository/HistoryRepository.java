package com.example.demo.repository;

import com.example.demo.entity.Artist;
import com.example.demo.entity.History;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByUser_UserIdOrderByPlayedAtDesc(Long userId);

    List<History> findTop50ByUser_UserIdOrderByPlayedAtDesc(Long userId);

    List<History> findByUser_UserIdAndPlaylist_PlaylistIdOrderByPlayedAtDesc(Long userId, Long playlistId);

    // ADDED: Generic count method just in case we need total raw play counts
    long countByUser(User user);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    @Query("SELECT COALESCE(SUM(s.duration), 0) FROM History h JOIN h.song s WHERE h.user = :user")
    Long calculateTotalListeningTimeInSeconds(@Param("user") User user);

    // --- NEW: For Artist Analytics ---
    List<History> findBySong_Artist(Artist artist);
}