package org.example.revplayplaybackservice.repository;

import org.example.revplayplaybackservice.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByUserEmailOrderByPlayedAtDesc(String userEmail);

    List<History> findTop50ByUserEmailOrderByPlayedAtDesc(String userEmail);

    List<History> findByUserEmailAndPlaylistIdOrderByPlayedAtDesc(String userEmail, Long playlistId);

    void deleteByUserEmail(String userEmail);

    @Query("SELECT SUM(h.songDuration) FROM History h WHERE h.userEmail = :email")
    Long calculateTotalListeningTimeInSeconds(@Param("email") String email);
}