package org.example.revplayanalyticsservice.controller;

import org.example.revplayanalyticsservice.dto.*;
import org.example.revplayanalyticsservice.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Dashboard (Total plays, songs, top songs, total favorites)
    @GetMapping("/artist/stats")
    public ResponseEntity<ArtistStatsResponse> getAnalytics(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(analyticsService.getArtistAnalytics(token));
    }

    // Top Listeners (Fans who played my music the most)
    @GetMapping("/artist/top-listeners")
    public ResponseEntity<List<TopListenerDTO>> getTopListeners(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(analyticsService.getTopListeners(token));
    }

    // Daily Listening Trends
    @GetMapping("/artist/trends")
    public ResponseEntity<List<TrendDTO>> getListeningTrends(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(analyticsService.getListeningTrends(token));
    }
}