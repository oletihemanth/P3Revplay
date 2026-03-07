package org.example.revplayanalyticsservice.service;

import org.example.revplayanalyticsservice.client.CatalogClient;
import org.example.revplayanalyticsservice.client.PlaybackClient;
import org.example.revplayanalyticsservice.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final CatalogClient catalogClient;
    private final PlaybackClient playbackClient;

    public AnalyticsService(CatalogClient catalogClient, PlaybackClient playbackClient) {
        this.catalogClient = catalogClient;
        this.playbackClient = playbackClient;
    }

    // --- ARTIST DASHBOARD ---
    public ArtistStatsResponse getArtistAnalytics(String token) {
        // Fetch raw stats from the Catalog Service Walkie-Talkie
        Map<String, Object> rawStats = catalogClient.getArtistSongStats(token);

        ArtistStatsResponse response = new ArtistStatsResponse();
        response.setArtistName((String) rawStats.get("artistName"));
        response.setGenre((String) rawStats.get("genre"));
        response.setTotalSongsUploaded((Integer) rawStats.get("totalSongs"));
        response.setTotalAllTimePlays(((Number) rawStats.get("totalPlays")).longValue());

        // We will mock total favorites for now until the Favorite Service Walkie-Talkie is fully wired
        response.setTotalFavorites(0L);

        return response;
    }

    // --- TOP LISTENERS ---
    public List<TopListenerDTO> getTopListeners(String token) {
        List<Map<String, Object>> rawData = playbackClient.getTopListenersForArtist(token);

        return rawData.stream().map(data -> new TopListenerDTO(
                (String) data.get("userName"),
                (String) data.get("profilePictureUrl"),
                ((Number) data.get("totalPlays")).longValue()
        )).collect(Collectors.toList());
    }

    // --- LISTENING TRENDS ---
    public List<TrendDTO> getListeningTrends(String token) {
        List<Map<String, Object>> rawData = playbackClient.getListeningTrendsForArtist(token);

        return rawData.stream().map(data -> new TrendDTO(
                (String) data.get("date"),
                ((Number) data.get("playCount")).longValue()
        )).collect(Collectors.toList());
    }
}