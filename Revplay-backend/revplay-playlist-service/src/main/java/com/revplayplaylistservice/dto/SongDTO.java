package com.revplayplaylistservice.dto;

import lombok.Data;

@Data
public class SongDTO {
    private Long songId;
    private String title;
    private String genre;
    private int duration;
    private int playCount;
    private String audioFileUrl;
    private String coverImageUrl;
    private String artistName;
}