package com.revplayplaylistservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlaylistResponse {
    private Long playlistId;
    private String name;
    private String description;
    private String privacy;
    private String creatorName;
    private String coverImageUrl;
    private List<SongDTO> songs; // We will fetch these from the Catalog Service!
}