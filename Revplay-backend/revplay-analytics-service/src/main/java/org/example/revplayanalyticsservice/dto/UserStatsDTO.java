package org.example.revplayanalyticsservice.dto;
import lombok.Data;

@Data
public class UserStatsDTO {
    private long totalPlaylists;
    private long favoriteSongsCount;
    private long totalListeningTimeMinutes;
}