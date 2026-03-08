package org.example.revplayfavoriteservice.client;

import org.example.revplayfavoriteservice.dto.SongDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "REVPLAY-CATALOG-SERVICE")
public interface CatalogClient {

    //  FIX: Added @RequestHeader to forward the token to the Catalog Service
    @GetMapping("/api/songs/{songId}")
    SongDTO getSongById(@RequestHeader("Authorization") String token, @PathVariable("songId") Long songId);
}