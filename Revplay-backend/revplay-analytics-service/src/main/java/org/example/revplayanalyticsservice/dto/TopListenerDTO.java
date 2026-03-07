package org.example.revplayanalyticsservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopListenerDTO {
    private String userName;
    private String profilePictureUrl;
    private Long totalPlays;
}