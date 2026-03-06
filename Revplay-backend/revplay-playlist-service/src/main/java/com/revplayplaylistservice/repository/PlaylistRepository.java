package com.revplayplaylistservice.repository;

import com.revplayplaylistservice.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserEmail(String email);
    List<Playlist> findByPrivacy(String privacy);
}