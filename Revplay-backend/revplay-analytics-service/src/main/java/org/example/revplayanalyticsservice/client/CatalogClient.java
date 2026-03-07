package org.example.revplayanalyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "REVPLAY-CATALOG-SERVICE")
public interface CatalogClient {
    // We will add this to Catalog Service later!
    @GetMapping("/api/songs/internal/artist/stats")
    Map<String, Object> getArtistSongStats(@RequestHeader("Authorization") String token);
}