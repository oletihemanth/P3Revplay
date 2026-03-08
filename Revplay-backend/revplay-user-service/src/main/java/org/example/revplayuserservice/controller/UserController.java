package org.example.revplayuserservice.controller;

import org.example.revplayuserservice.dto.UserProfileDTO;
import org.example.revplayuserservice.dto.UserStatsDTO;
import org.example.revplayuserservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserProfileDTO profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    //  FIX: Changed the URL to "/me" and added @RequestBody to accept JSON!
    @PutMapping("/me")
    public ResponseEntity<String> updateDisplayName(Authentication authentication, @RequestBody Map<String, String> payload) {
        String newName = payload.get("name");
        userService.updateDisplayName(authentication.getName(), newName);
        return ResponseEntity.ok("{\"message\": \"Display name updated successfully.\"}");
    }

    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(Authentication authentication) {
        String email = authentication.getName();
        UserStatsDTO stats = userService.getUserStats(email);
        return ResponseEntity.ok(stats);
    }

    // --- NEW: Walkie-Talkie Receiver for the Catalog Service! ---
    // The Catalog Service will call this to find out who uploaded the song
    @GetMapping("/{email}")
    public ResponseEntity<UserProfileDTO> getUserByEmail(@PathVariable("email") String email) {
        UserProfileDTO profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }
}