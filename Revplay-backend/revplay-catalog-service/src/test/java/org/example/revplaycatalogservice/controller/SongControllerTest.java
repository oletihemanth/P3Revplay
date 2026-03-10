package org.example.revplaycatalogservice.controller;

import org.example.revplaycatalogservice.dto.SongDTO;
import org.example.revplaycatalogservice.security.JwtAuthenticationFilter;
import org.example.revplaycatalogservice.service.FileStorageService;
import org.example.revplaycatalogservice.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//  FIX: New Spring Boot 4.0 Test Imports!
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SongController.class)
@AutoConfigureMockMvc(addFilters = false)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc; // Our "fake web browser"

    //  FIX: @MockBean was removed in Spring Boot 4.0. We now use @MockitoBean!
    @MockitoBean private SongService songService;
    @MockitoBean private FileStorageService fileStorageService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private SongDTO mockSong;
    private Authentication mockAuth;

    @BeforeEach
    void setUp() {
        // Setup fake song data
        mockSong = new SongDTO();
        mockSong.setSongId(100L);
        mockSong.setTitle("Aaya Sher");
        mockSong.setArtistName("Sagar");

        // Setup a fake logged-in user to bypass Authentication requirements
        mockAuth = new UsernamePasswordAuthenticationToken("artist@gmail.com", null);
    }

    // --- TEST 1: GET ALL SONGS ---
    @Test
    void getAllSongs_ShouldReturn200AndList() throws Exception {
        // Arrange
        when(songService.getAllSongs()).thenReturn(List.of(mockSong));

        // Act & Assert
        mockMvc.perform(get("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Aaya Sher"))
                .andExpect(jsonPath("$[0].artistName").value("Sagar"));
    }

    // --- TEST 2: GET SONG BY ID ---
    @Test
    void getSongById_ShouldReturn200AndSong() throws Exception {
        // Arrange
        when(songService.getSongById(100L)).thenReturn(mockSong);

        // Act & Assert
        mockMvc.perform(get("/api/songs/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Aaya Sher"));
    }

    // --- TEST 3: INCREMENT PLAY COUNT ---
    @Test
    void incrementPlayCount_ShouldReturn200AndMessage() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/songs/100/increment-play")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Play count updated successfully"));
    }

    // --- TEST 4: UPLOAD SONG (Multipart Form Data) ---
    @Test
    void uploadSong_ShouldReturn200() throws Exception {
        // Arrange: Create a fake MP3 file
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "fake audio bytes".getBytes());

        when(songService.uploadSong(eq("artist@gmail.com"), eq("Aaya Sher"), eq("Pop"), eq(0), eq("PUBLIC"), eq(null), any(), any()))
                .thenReturn(mockSong);

        // Act & Assert
        mockMvc.perform(multipart("/api/songs")
                        .file(file)
                        .param("title", "Aaya Sher")
                        .param("genre", "Pop")
                        .principal(mockAuth)) // Attach our fake user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Aaya Sher"));
    }
}