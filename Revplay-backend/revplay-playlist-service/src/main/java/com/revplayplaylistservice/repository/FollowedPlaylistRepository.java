package com.revplayplaylistservice.repository;

import com.revplayplaylistservice.entity.FollowedPlaylist;
import com.revplayplaylistservice.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FollowedPlaylistRepository extends JpaRepository<FollowedPlaylist, Long> {
    Optional<FollowedPlaylist> findByUserEmailAndPlaylist(String email, Playlist playlist);
}