package com.revplayplaylistservice.repository;

import com.revplayplaylistservice.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserEmailAndSongId(String email, Long songId);
    List<Favorite> findByUserEmail(String email);
    long countByUserEmail(String email);
}