package org.example.revplayplaybackservice.controller;

import org.example.revplayplaybackservice.dto.HistoryDTO;
import org.example.revplayplaybackservice.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/log")
    public ResponseEntity<String> logPlay(@RequestHeader("Authorization") String token,
                                          @RequestParam Long songId,
                                          @RequestParam(required = false) Long playlistId,
                                          Authentication authentication) {
        historyService.logSongPlay(token, authentication.getName(), songId, playlistId);
        return ResponseEntity.ok("{\"message\": \"Song play logged successfully.\"}");
    }

    @GetMapping("/recent")
    public ResponseEntity<List<HistoryDTO>> getRecentHistory(@RequestHeader("Authorization") String token,
                                                             Authentication authentication) {
        return ResponseEntity.ok(historyService.getRecentHistory(token, authentication.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<HistoryDTO>> getCompleteHistory(@RequestHeader("Authorization") String token,
                                                               Authentication authentication) {
        return ResponseEntity.ok(historyService.getCompleteHistory(token, authentication.getName()));
    }

    @GetMapping("/playlist/{playlistId}")
    public ResponseEntity<List<HistoryDTO>> getPlaylistHistory(@RequestHeader("Authorization") String token,
                                                               @PathVariable Long playlistId,
                                                               Authentication authentication) {
        return ResponseEntity.ok(historyService.getPlaylistHistory(token, authentication.getName(), playlistId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearHistory(Authentication authentication) {
        historyService.clearHistory(authentication.getName());
        return ResponseEntity.ok("{\"message\": \"Listening history cleared successfully.\"}");
    }

    @GetMapping("/stats/time")
    public ResponseEntity<String> getTotalTime(Authentication authentication) {
        String timeStats = historyService.getTotalListeningTime(authentication.getName());
        return ResponseEntity.ok("{\"totalListeningTime\": \"" + timeStats + "\"}");
    }

    // ---  NEW: INTERNAL ANALYTICS ENDPOINTS  ---
    @GetMapping("/internal/artist/top-listeners")
    public ResponseEntity<List<Map<String, Object>>> getTopListeners(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(historyService.getTopListenersForArtist(token));
    }

    @GetMapping("/internal/artist/trends")
    public ResponseEntity<List<Map<String, Object>>> getListeningTrends(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(historyService.getListeningTrendsForArtist(token));
    }
}