package com.example.demo.repository;

import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByTitleContainingIgnoreCase(String title);

    List<Song> findByGenreIgnoreCase(String genre);
}
