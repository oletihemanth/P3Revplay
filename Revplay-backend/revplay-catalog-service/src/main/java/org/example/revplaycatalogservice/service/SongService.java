package org.example.revplaycatalogservice.service;

import org.example.revplaycatalogservice.client.UserClient;
import org.example.revplaycatalogservice.dto.SongDTO;
import org.example.revplaycatalogservice.dto.UserDTO;
import org.example.revplaycatalogservice.entity.Album;
import org.example.revplaycatalogservice.entity.Artist;
import org.example.revplaycatalogservice.entity.LikedSong;
import org.example.revplaycatalogservice.entity.Song;
import org.example.revplaycatalogservice.exception.ResourceNotFoundException;
import org.example.revplaycatalogservice.repository.AlbumRepository;
import org.example.revplaycatalogservice.repository.ArtistRepository;
import org.example.revplaycatalogservice.repository.LikedSongRepository;
import org.example.revplaycatalogservice.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final FileStorageService fileStorageService;
    private final LikedSongRepository likedSongRepository;
    private final UserClient userClient;

    public SongService(SongRepository songRepository, ArtistRepository artistRepository,
                       AlbumRepository albumRepository, FileStorageService fileStorageService,
                       LikedSongRepository likedSongRepository, UserClient userClient) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.fileStorageService = fileStorageService;
        this.likedSongRepository = likedSongRepository;
        this.userClient = userClient;
    }

    public List<SongDTO> getAllSongs() {
        return songRepository.findByVisibility("PUBLIC").stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<SongDTO> searchSongsByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCaseAndVisibility(title, "PUBLIC")
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<SongDTO> filterSongs(String genre, String artistName, String albumName, Integer releaseYear) {
        return songRepository.filterSongs(genre, artistName, albumName, releaseYear)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public SongDTO getSongById(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        return mapToDTO(song);
    }

    public List<SongDTO> getMyUploadedSongs(String email) {
        Artist artist = getArtistByEmail(email);
        return songRepository.findByArtist(artist)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleLikeSong(String email, Long songId) {
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        Optional<LikedSong> existingLike = likedSongRepository.findByEmailAndSong(email, song);
        if (existingLike.isPresent()) {
            likedSongRepository.delete(existingLike.get());
            return false;
        } else {
            likedSongRepository.save(new LikedSong(email, song));
            return true;
        }
    }

    public boolean isSongLikedByUser(String email, Long songId) {
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        return likedSongRepository.existsByEmailAndSong(email, song);
    }

    public List<SongDTO> getLikedSongs(String email) {
        return likedSongRepository.findByEmail(email).stream()
                .map(LikedSong::getSong)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SongDTO uploadSong(String email, String title, String genre, Integer duration,
                              String visibility, Long albumId, MultipartFile audioFile, MultipartFile coverImage) {
        Artist artist = getArtistByEmail(email);
        String audioFileName = fileStorageService.storeFile(audioFile);
        String coverImageName = coverImage != null ? fileStorageService.storeFile(coverImage) : null;

        if (audioFileName == null) throw new RuntimeException("Audio file is required!");

        Song song = new Song();
        song.setTitle(title);
        song.setGenre(genre);
        song.setDuration(duration != null ? duration : 0);
        song.setVisibility(visibility != null ? visibility : "PUBLIC");
        song.setArtist(artist);
        song.setAudioFileUrl(audioFileName);
        song.setCoverImageUrl(coverImageName);

        if (albumId != null) {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
            if (!album.getArtist().getArtistId().equals(artist.getArtistId())) {
                throw new RuntimeException("You do not own this album!");
            }
            song.setAlbum(album);
        }

        return mapToDTO(songRepository.save(song));
    }

    @Transactional
    public SongDTO updateSong(String email, Long songId, String title, String genre, String visibility) {
        Artist artist = getArtistByEmail(email);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!song.getArtist().getArtistId().equals(artist.getArtistId())) {
            throw new RuntimeException("You can only update your own songs!");
        }

        song.setTitle(title);
        song.setGenre(genre);
        song.setVisibility(visibility);

        return mapToDTO(songRepository.save(song));
    }

    @Transactional
    public void deleteSong(String email, Long songId) {
        Artist artist = getArtistByEmail(email);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!song.getArtist().getArtistId().equals(artist.getArtistId())) {
            throw new RuntimeException("You can only delete your own songs!");
        }

        fileStorageService.deleteFile(song.getAudioFileUrl());
        if(song.getCoverImageUrl() != null) fileStorageService.deleteFile(song.getCoverImageUrl());

        songRepository.delete(song);
    }

    @Transactional
    public void incrementPlayCount(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        Long currentCount = song.getPlayCount() != null ? song.getPlayCount() : 0L;
        song.setPlayCount(currentCount + 1L);
        songRepository.save(song);
    }

    // ---  NEW: INTERNAL ANALYTICS METHODS  ---
    public Map<String, Object> getArtistSongStats(String email) {
        Artist artist = getArtistByEmail(email);
        List<Song> artistSongs = songRepository.findByArtist(artist);
        long totalPlays = artistSongs.stream().mapToLong(s -> s.getPlayCount() != null ? s.getPlayCount() : 0).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("artistName", artist.getArtistName());
        stats.put("genre", artist.getGenre());
        stats.put("totalSongs", artistSongs.size());
        stats.put("totalPlays", totalPlays);
        return stats;
    }

    public List<Long> getArtistSongIds(String email) {
        Artist artist = getArtistByEmail(email);
        return songRepository.findByArtist(artist).stream()
                .map(Song::getSongId)
                .collect(Collectors.toList());
    }

    private Artist getArtistByEmail(String email) {
        return artistRepository.findByEmail(email).orElseGet(() -> {
            System.out.println("Auto-creating Artist profile for: " + email);
            Artist newArtist = new Artist();
            newArtist.setEmail(email);

            String displayName = email.split("@")[0];

            try {
                UserDTO userProfile = userClient.getUserByEmail(email);
                if (userProfile != null && userProfile.getName() != null) {
                    displayName = userProfile.getName();
                }
            } catch (Exception e) {
                System.out.println("Walkie-Talkie Failed (User Service might be down). Using fallback name.");
            }

            newArtist.setArtistName(displayName);
            return artistRepository.save(newArtist);
        });
    }

    private SongDTO mapToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setDuration(song.getDuration());
        dto.setPlayCount(song.getPlayCount());
        dto.setAudioFileUrl(song.getAudioFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());

        if (song.getArtist() != null) dto.setArtistName(song.getArtist().getArtistName());
        if (song.getAlbum() != null) dto.setAlbumName(song.getAlbum().getTitle());
        return dto;
    }
}