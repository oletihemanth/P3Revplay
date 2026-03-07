package org.example.revplayplaybackservice.service;

import org.example.revplayplaybackservice.client.CatalogClient;
import org.example.revplayplaybackservice.dto.HistoryDTO;
import org.example.revplayplaybackservice.dto.SongDTO;
import org.example.revplayplaybackservice.entity.History;
import org.example.revplayplaybackservice.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final CatalogClient catalogClient;

    public HistoryService(HistoryRepository historyRepository, CatalogClient catalogClient) {
        this.historyRepository = historyRepository;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public void logSongPlay(String token, String email, Long songId, Long playlistId) {
        SongDTO song = catalogClient.getSongById(token, songId);

        try {
            catalogClient.incrementPlayCount(token, songId);
        } catch (Exception e) {
            System.out.println("Could not increment play count, but logging history anyway.");
        }

        History history = new History(email, songId, playlistId, song.getDuration());
        historyRepository.save(history);
    }

    public List<HistoryDTO> getCompleteHistory(String token, String email) {
        return historyRepository.findByUserEmailOrderByPlayedAtDesc(email)
                .stream().map(h -> mapToDTO(token, h)).collect(Collectors.toList());
    }

    public List<HistoryDTO> getRecentHistory(String token, String email) {
        return historyRepository.findTop50ByUserEmailOrderByPlayedAtDesc(email)
                .stream().map(h -> mapToDTO(token, h)).collect(Collectors.toList());
    }

    public List<HistoryDTO> getPlaylistHistory(String token, String email, Long playlistId) {
        return historyRepository.findByUserEmailAndPlaylistIdOrderByPlayedAtDesc(email, playlistId)
                .stream().map(h -> mapToDTO(token, h)).collect(Collectors.toList());
    }

    @Transactional
    public void clearHistory(String email) {
        historyRepository.deleteByUserEmail(email);
    }

    public String getTotalListeningTime(String email) {
        Long totalSeconds = historyRepository.calculateTotalListeningTimeInSeconds(email);

        if (totalSeconds == null || totalSeconds == 0) return "0 minutes";

        long minutes = totalSeconds / 60;
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " hours, " + remainingMinutes + " minutes";
        }
    }

    // ---  NEW: INTERNAL ANALYTICS METHODS  ---
    public List<Map<String, Object>> getTopListenersForArtist(String token) {
        List<Long> songIds = catalogClient.getArtistSongIds(token);
        if (songIds == null || songIds.isEmpty()) return new ArrayList<>();

        List<Object[]> results = historyRepository.findTopListenersBySongIds(songIds);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            String userEmail = row[0].toString();
            map.put("userName", userEmail.split("@")[0]); // Temporary Display Name
            map.put("profilePictureUrl", "");
            map.put("totalPlays", ((Number) row[1]).longValue());
            response.add(map);
        }
        return response;
    }

    public List<Map<String, Object>> getListeningTrendsForArtist(String token) {
        List<Long> songIds = catalogClient.getArtistSongIds(token);
        if (songIds == null || songIds.isEmpty()) return new ArrayList<>();

        List<Object[]> results = historyRepository.findTrendsBySongIds(songIds);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0].toString());
            map.put("playCount", ((Number) row[1]).longValue());
            response.add(map);
        }
        return response;
    }

    private HistoryDTO mapToDTO(String token, History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setSongId(history.getSongId());
        dto.setPlayedAt(history.getPlayedAt());
        dto.setPlaylistId(history.getPlaylistId());

        try {
            SongDTO song = catalogClient.getSongById(token, history.getSongId());
            dto.setSongTitle(song.getTitle());
            dto.setCoverImageUrl(song.getCoverImageUrl());
            dto.setArtistName(song.getArtistName());
        } catch (Exception e) {
            dto.setSongTitle("Unknown Song");
        }
        return dto;
    }
}