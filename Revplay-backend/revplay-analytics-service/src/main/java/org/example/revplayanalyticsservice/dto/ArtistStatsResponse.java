package org.example.revplayanalyticsservice.dto;
import lombok.Data;
import java.util.List;

@Data
public class ArtistStatsResponse {
    private String artistName;
    private String genre;
    private int totalSongsUploaded;
    private long totalAllTimePlays;
    private long totalFavorites;
    private List<SongPerformanceDTO> topSongs;
}