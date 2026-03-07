package org.example.revplayplaybackservice.client;

import org.example.revplayplaybackservice.dto.SongDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "REVPLAY-CATALOG-SERVICE")
public interface CatalogClient {

    @GetMapping("/api/songs/{songId}")
    SongDTO getSongById(@RequestHeader("Authorization") String token, @PathVariable("songId") Long songId);

    @PutMapping("/api/songs/{songId}/increment-play")
    void incrementPlayCount(@RequestHeader("Authorization") String token, @PathVariable("songId") Long songId);
}