package org.example.revplaycatalogservice.service;

import org.example.revplaycatalogservice.client.UserClient;
import org.example.revplaycatalogservice.dto.SongDTO;
import org.example.revplaycatalogservice.entity.Artist;
import org.example.revplaycatalogservice.entity.LikedSong;
import org.example.revplaycatalogservice.entity.Song;
import org.example.revplaycatalogservice.repository.AlbumRepository;
import org.example.revplaycatalogservice.repository.ArtistRepository;
import org.example.revplaycatalogservice.repository.LikedSongRepository;
import org.example.revplaycatalogservice.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    // 1. Create "Fake" dependencies (Mocks) so we don't touch the real Database
    @Mock private SongRepository songRepository;
    @Mock private ArtistRepository artistRepository;
    @Mock private AlbumRepository albumRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private LikedSongRepository likedSongRepository;
    @Mock private UserClient userClient;

    // 2. Inject those fakes into the real SongService
    @InjectMocks
    private SongService songService;

    // Dummy data for our tests
    private Artist testArtist;
    private Song testSong;

    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setArtistId(1L);
        testArtist.setEmail("artist@gmail.com");
        testArtist.setArtistName("Sagar");

        testSong = new Song();
        testSong.setSongId(100L);
        testSong.setTitle("Aaya Sher");
        testSong.setArtist(testArtist);
        testSong.setPlayCount(5L);
    }

    // --- TEST 1: TOGGLE LIKE (ADDING A NEW LIKE) ---
    @Test
    void toggleLikeSong_WhenNotLiked_ShouldAddLike() {
        // Arrange: Tell the fake repository to pretend the song exists, but the like does not.
        when(songRepository.findById(100L)).thenReturn(Optional.of(testSong));
        when(likedSongRepository.findByEmailAndSong("user@gmail.com", testSong)).thenReturn(Optional.empty());

        // Act: Call the real method
        boolean isLiked = songService.toggleLikeSong("user@gmail.com", 100L);

        // Assert: Verify it returned true and called save()
        assertTrue(isLiked);
        verify(likedSongRepository, times(1)).save(any(LikedSong.class));
    }

    // --- TEST 2: TOGGLE LIKE (REMOVING AN EXISTING LIKE) ---
    @Test
    void toggleLikeSong_WhenAlreadyLiked_ShouldRemoveLike() {
        // Arrange: Pretend the like already exists
        LikedSong existingLike = new LikedSong("user@gmail.com", testSong);
        when(songRepository.findById(100L)).thenReturn(Optional.of(testSong));
        when(likedSongRepository.findByEmailAndSong("user@gmail.com", testSong)).thenReturn(Optional.of(existingLike));

        // Act
        boolean isLiked = songService.toggleLikeSong("user@gmail.com", 100L);

        // Assert: Verify it returned false and called delete()
        assertFalse(isLiked);
        verify(likedSongRepository, times(1)).delete(existingLike);
    }

    // --- TEST 3: INCREMENT PLAY COUNT ---
    @Test
    void incrementPlayCount_ShouldCallAtomicQuery() {
        // Act
        songService.incrementPlayCount(100L);

        // Assert: Ensure the service calls the specific atomic database query
        verify(songRepository, times(1)).incrementPlayCountAtomically(100L);
    }

    // --- TEST 4: UPLOAD SONG ---
    @Test
    void uploadSong_Success() {
        // Arrange
        MultipartFile fakeAudio = new MockMultipartFile("file", "audio.mp3", "audio/mpeg", "dummy audio content".getBytes());
        MultipartFile fakeCover = new MockMultipartFile("coverImage", "cover.jpg", "image/jpeg", "dummy image content".getBytes());

        when(artistRepository.findByEmail("artist@gmail.com")).thenReturn(Optional.of(testArtist));
        when(fileStorageService.storeFile(fakeAudio)).thenReturn("saved_audio.mp3");
        when(fileStorageService.storeFile(fakeCover)).thenReturn("saved_cover.jpg");

        // When the repo saves the song, return a mock saved song with an ID
        when(songRepository.save(any(Song.class))).thenAnswer(invocation -> {
            Song savedSong = invocation.getArgument(0);
            savedSong.setSongId(999L); // Simulate database auto-generating an ID
            return savedSong;
        });

        // Act
        SongDTO result = songService.uploadSong(
                "artist@gmail.com", "My New Track", "Pop", 180, "PUBLIC", null, fakeAudio, fakeCover
        );

        // Assert
        assertNotNull(result);
        assertEquals("My New Track", result.getTitle());
        assertEquals("Pop", result.getGenre());
        assertEquals("Sagar", result.getArtistName());
        verify(songRepository, times(1)).save(any(Song.class));
    }
}