package com.revplayplaylistservice.controller;

import com.revplayplaylistservice.dto.PlaylistResponse;
import com.revplayplaylistservice.service.CurationService;
// 🚨 FIX: Added missing security imports
import com.revplayplaylistservice.security.JwtAuthenticationFilter;
import com.revplayplaylistservice.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaylistController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security for tests
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurationService curationService;

    // 🚨 FIX: Fake the security beans so the application context doesn't crash!
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private Authentication mockAuth;
    private PlaylistResponse mockPlaylist;

    @BeforeEach
    void setUp() {
        mockAuth = new UsernamePasswordAuthenticationToken("sagar@gmail.com", null);

        mockPlaylist = new PlaylistResponse();
        mockPlaylist.setPlaylistId(1L);
        mockPlaylist.setName("Workout Vibes");
        mockPlaylist.setCreatorName("Sagar");
    }

    // --- TEST 1: CREATE PLAYLIST ENDPOINT ---
    @Test
    void createPlaylist_ShouldReturn200() throws Exception {
        when(curationService.createPlaylist(eq("sagar@gmail.com"), eq("Workout Vibes"), eq("Pump it up"), eq("PUBLIC"), any()))
                .thenReturn(mockPlaylist);

        mockMvc.perform(multipart("/api/playlists")
                        .param("name", "Workout Vibes")
                        .param("description", "Pump it up")
                        .param("privacy", "PUBLIC")
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Workout Vibes"));
    }

    // --- TEST 2: ADD SONG TO PLAYLIST ENDPOINT ---
    @Test
    void addSongToPlaylist_ShouldReturn200() throws Exception {
        when(curationService.addSongToPlaylist("sagar@gmail.com", 1L, 100L))
                .thenReturn("Song added to playlist!");

        mockMvc.perform(post("/api/playlists/1/songs/100")
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Song added to playlist!"));
    }

    // --- TEST 3: REMOVE SONG FROM PLAYLIST ENDPOINT ---
    @Test
    void removeSongFromPlaylist_ShouldReturn200() throws Exception {
        when(curationService.removeSongFromPlaylist("sagar@gmail.com", 1L, 100L))
                .thenReturn("Song removed from playlist.");

        mockMvc.perform(delete("/api/playlists/1/songs/100")
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Song removed from playlist."));
    }
}