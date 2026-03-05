package org.example.revplayuserservice.dto;

public class UserStatsDTO {
    private long totalPlaylists;
    private long favoriteSongsCount;
    private long totalListeningTimeMinutes;

    public long getTotalPlaylists() { return totalPlaylists; }
    public void setTotalPlaylists(long totalPlaylists) { this.totalPlaylists = totalPlaylists; }

    public long getFavoriteSongsCount() { return favoriteSongsCount; }
    public void setFavoriteSongsCount(long favoriteSongsCount) { this.favoriteSongsCount = favoriteSongsCount; }

    public long getTotalListeningTimeMinutes() { return totalListeningTimeMinutes; }
    public void setTotalListeningTimeMinutes(long totalListeningTimeMinutes) { this.totalListeningTimeMinutes = totalListeningTimeMinutes; }
}