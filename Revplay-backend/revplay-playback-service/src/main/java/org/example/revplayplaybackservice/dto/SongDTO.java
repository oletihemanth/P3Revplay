package org.example.revplayplaybackservice.dto;
import lombok.Data;

@Data
public class SongDTO {
    private Long songId;
    private String title;
    private int duration;
    private String coverImageUrl;
    private String artistName;
}