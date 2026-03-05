package org.example.revplayuserservice.controller;

import org.example.revplayuserservice.dto.UserProfileDTO;
import org.example.revplayuserservice.dto.UserStatsDTO;
import org.example.revplayuserservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/me/name")
    public ResponseEntity<String> updateDisplayName(Authentication authentication, @RequestParam String name) {
        userService.updateDisplayName(authentication.getName(), name);
        return ResponseEntity.ok("{\"message\": \"Display name updated successfully.\"}");
    }

    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(Authentication authentication) {
        String email = authentication.getName();
        UserStatsDTO stats = userService.getUserStats(email);
        return ResponseEntity.ok(stats);
    }
}