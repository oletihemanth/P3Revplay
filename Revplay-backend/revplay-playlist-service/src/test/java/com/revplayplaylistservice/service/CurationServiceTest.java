package com.revplayplaylistservice.service;

import com.revplayplaylistservice.client.CatalogClient;
import com.revplayplaylistservice.client.UserClient;
import com.revplayplaylistservice.dto.PlaylistResponse;
import com.revplayplaylistservice.dto.UserDTO;
import com.revplayplaylistservice.entity.Playlist;
import com.revplayplaylistservice.entity.PlaylistSong;
import com.revplayplaylistservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurationServiceTest {

    // Mocking all Repositories and Feign Clients
    @Mock private FavoriteRepository favoriteRepository;
    @Mock private PlaylistRepository playlistRepository;
    @Mock private PlaylistSongRepository playlistSongRepository;
    @Mock private FollowedPlaylistRepository followedPlaylistRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private UserClient userClient;
    @Mock private CatalogClient catalogClient;

    @InjectMocks
    private CurationService curationService;

    private Playlist testPlaylist;

    @BeforeEach
    void setUp() {
        testPlaylist = new Playlist();
        testPlaylist.setPlaylistId(1L);
        testPlaylist.setUserEmail("sagar@gmail.com");
        testPlaylist.setName("Workout Vibes");
        testPlaylist.setPrivacy("PUBLIC");
    }

    // --- TEST 1: CREATE PLAYLIST ---
    @Test
    void createPlaylist_Success() {
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        // Mock user client to avoid exception during mapping
        UserDTO mockUser = new UserDTO();
        mockUser.setName("Sagar");
        when(userClient.getUserByEmail("sagar@gmail.com")).thenReturn(mockUser);

        PlaylistResponse response = curationService.createPlaylist("sagar@gmail.com", "Workout Vibes", "Pump it up", "PUBLIC", null);

        assertNotNull(response);
        assertEquals("Workout Vibes", response.getName());
        assertEquals("Sagar", response.getCreatorName());
        verify(playlistRepository, times(1)).save(any(Playlist.class));
    }

    // --- TEST 2: ADD SONG (SUCCESS) ---
    @Test
    void addSongToPlaylist_Success() {
        // Arrange: User is the owner, and song is not in the playlist yet
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));
        when(playlistSongRepository.findByPlaylistAndSongId(testPlaylist, 100L)).thenReturn(Optional.empty());

        // Act
        String result = curationService.addSongToPlaylist("sagar@gmail.com", 1L, 100L);

        // Assert
        assertEquals("Song added to playlist!", result);
        verify(playlistSongRepository, times(1)).save(any(PlaylistSong.class));
    }

    // --- TEST 3: ADD SONG (SECURITY/OWNERSHIP CHECK) ---
    @Test
    void addSongToPlaylist_WhenNotOwner_ShouldThrowException() {
        // Arrange: Someone else owns this playlist
        testPlaylist.setUserEmail("otheruser@gmail.com");
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            curationService.addSongToPlaylist("sagar@gmail.com", 1L, 100L);
        });

        assertEquals("Unauthorized: You do not own this playlist", exception.getMessage());
    }

    // --- TEST 4: REMOVE SONG ---
    @Test
    void removeSongFromPlaylist_Success() {
        PlaylistSong existingSong = new PlaylistSong();
        existingSong.setPlaylist(testPlaylist);
        existingSong.setSongId(100L);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));
        when(playlistSongRepository.findByPlaylistAndSongId(testPlaylist, 100L)).thenReturn(Optional.of(existingSong));

        String result = curationService.removeSongFromPlaylist("sagar@gmail.com", 1L, 100L);

        assertEquals("Song removed from playlist.", result);
        verify(playlistSongRepository, times(1)).delete(existingSong);
    }
}