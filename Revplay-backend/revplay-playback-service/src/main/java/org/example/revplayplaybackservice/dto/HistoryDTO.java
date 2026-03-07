package org.example.revplayplaybackservice.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistoryDTO {
    private Long historyId;
    private Long songId;
    private String songTitle;
    private String coverImageUrl;
    private String artistName;
    private LocalDateTime playedAt;
    private Long playlistId;
}