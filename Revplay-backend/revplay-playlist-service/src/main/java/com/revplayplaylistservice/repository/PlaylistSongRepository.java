package com.revplayplaylistservice.repository;

import com.revplayplaylistservice.entity.Playlist;
import com.revplayplaylistservice.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {
    List<PlaylistSong> findByPlaylistOrderBySongOrderAsc(Playlist playlist);
    Optional<PlaylistSong> findByPlaylistAndSongId(Playlist playlist, Long songId);
}