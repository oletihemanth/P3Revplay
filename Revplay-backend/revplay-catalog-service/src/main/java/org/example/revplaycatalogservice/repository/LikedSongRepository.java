package org.example.revplaycatalogservice.repository;

import org.example.revplaycatalogservice.entity.LikedSong;
import org.example.revplaycatalogservice.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, Long> {

    Optional<LikedSong> findByEmailAndSong(String email, Song song);

    boolean existsByEmailAndSong(String email, Song song);

    List<LikedSong> findByEmail(String email);
}