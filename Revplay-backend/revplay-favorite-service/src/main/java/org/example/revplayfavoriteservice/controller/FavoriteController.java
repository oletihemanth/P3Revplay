package org.example.revplayfavoriteservice.controller;

import org.example.revplayfavoriteservice.dto.SongDTO;
import org.example.revplayfavoriteservice.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // FIX: Added @RequestHeader to capture the token
    @GetMapping
    public ResponseEntity<List<SongDTO>> getMyFavorites(@RequestHeader("Authorization") String token, Authentication authentication) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(token, authentication.getName()));
    }

    @GetMapping("/count")
    public ResponseEntity<String> getFavoritesCount(Authentication authentication) {
        long count = favoriteService.getFavoritesCount(authentication.getName());
        return ResponseEntity.ok("{\"count\": " + count + "}");
    }

    @PostMapping("/{songId}")
    public ResponseEntity<String> toggleFavorite(Authentication authentication, @PathVariable Long songId) {
        String message = favoriteService.toggleFavorite(authentication.getName(), songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }
}