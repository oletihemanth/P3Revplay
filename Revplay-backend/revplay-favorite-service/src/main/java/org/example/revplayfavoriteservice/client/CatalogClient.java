package org.example.revplayfavoriteservice.client;

import org.example.revplayfavoriteservice.dto.SongDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "REVPLAY-CATALOG-SERVICE")
public interface CatalogClient {
    @GetMapping("/api/songs/{songId}")
    SongDTO getSongById(@PathVariable("songId") Long songId);
}