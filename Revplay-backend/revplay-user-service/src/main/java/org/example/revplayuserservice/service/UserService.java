package org.example.revplayuserservice.service;

import org.example.revplayuserservice.dto.UserProfileDTO;
import org.example.revplayuserservice.dto.UserStatsDTO;
import org.example.revplayuserservice.entity.User;
import org.example.revplayuserservice.exception.ResourceNotFoundException;
import org.example.revplayuserservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found."));

        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());

        // We will fetch this from the Catalog Service via FeignClient later!
        dto.setTotalPlays(0L);

        return dto;
    }

    @Transactional
    public void updateDisplayName(String email, String newName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(newName);
        userRepository.save(user);
    }

    public UserStatsDTO getUserStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserStatsDTO stats = new UserStatsDTO();

        // We will fetch these from the Playlist and Catalog Services via FeignClient later!
        stats.setTotalPlaylists(0L);
        stats.setFavoriteSongsCount(0L);
        stats.setTotalListeningTimeMinutes(0L);

        return stats;
    }
}