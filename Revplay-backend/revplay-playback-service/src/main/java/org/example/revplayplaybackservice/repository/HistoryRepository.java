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

    // ---  NEW: ANALYTICS AGGREGATION QUERIES  ---
    @Query("SELECT h.userEmail, COUNT(h) FROM History h WHERE h.songId IN :songIds GROUP BY h.userEmail ORDER BY COUNT(h) DESC")
    List<Object[]> findTopListenersBySongIds(@Param("songIds") List<Long> songIds);

    @Query("SELECT FUNCTION('DATE', h.playedAt), COUNT(h) FROM History h WHERE h.songId IN :songIds GROUP BY FUNCTION('DATE', h.playedAt) ORDER BY FUNCTION('DATE', h.playedAt) ASC")
    List<Object[]> findTrendsBySongIds(@Param("songIds") List<Long> songIds);
}