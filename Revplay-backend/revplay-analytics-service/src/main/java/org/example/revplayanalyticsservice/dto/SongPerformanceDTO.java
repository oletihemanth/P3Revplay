package org.example.revplayanalyticsservice.dto;
import lombok.Data;

@Data
public class SongPerformanceDTO {
    private Long songId;
    private String title;
    private Long playCount;
}