package com.example.demo.repository;

import com.example.demo.entity.Artist;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByArtist(Artist artist);

    List<Song> findByVisibility(String visibility);
    List<Song> findByTitleContainingIgnoreCaseAndVisibility(String title, String visibility);

    @Query("SELECT s FROM Song s LEFT JOIN s.album a LEFT JOIN s.artist art LEFT JOIN art.user u " +
            "WHERE s.visibility = 'PUBLIC' " +
            "AND (:genre IS NULL OR LOWER(s.genre) = LOWER(:genre)) " +
            "AND (:artistName IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :artistName, '%'))) " +
            "AND (:albumName IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :albumName, '%'))) " +
            "AND (:releaseYear IS NULL OR YEAR(a.releaseDate) = :releaseYear)")
    List<Song> filterSongs(@Param("genre") String genre,
                           @Param("artistName") String artistName,
                           @Param("albumName") String albumName,
                           @Param("releaseYear") Integer releaseYear);
}