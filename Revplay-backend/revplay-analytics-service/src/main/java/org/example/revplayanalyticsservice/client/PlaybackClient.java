package org.example.revplayanalyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "REVPLAY-PLAYBACK-SERVICE")
public interface PlaybackClient {
    // We will add these to Playback Service later!
    @GetMapping("/api/history/internal/artist/top-listeners")
    List<Map<String, Object>> getTopListenersForArtist(@RequestHeader("Authorization") String token);

    @GetMapping("/api/history/internal/artist/trends")
    List<Map<String, Object>> getListeningTrendsForArtist(@RequestHeader("Authorization") String token);
}