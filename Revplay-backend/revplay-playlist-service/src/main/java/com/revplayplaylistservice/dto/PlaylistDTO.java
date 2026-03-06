package com.revplayplaylistservice.dto;

import lombok.Data;

@Data
public class PlaylistDTO {
    private Long playlistId;
    private String name;
    private String description;
    private String privacy;
    private String creatorName;
    private int songCount;
}