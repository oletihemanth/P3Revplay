package com.revplayplaylistservice.dto;

import lombok.Data;

@Data
public class PlaylistRequest {
    private String name;
    private String description;
    private String privacy; // "PUBLIC" or "PRIVATE"
}