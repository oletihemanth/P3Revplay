package org.example.revplayfavoriteservice.repository;

import org.example.revplayfavoriteservice.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserEmailAndSongId(String userEmail, Long songId);
    List<Favorite> findByUserEmail(String userEmail);
    long countByUserEmail(String userEmail);
}