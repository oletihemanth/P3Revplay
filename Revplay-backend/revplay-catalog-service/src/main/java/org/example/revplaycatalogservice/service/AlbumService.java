package org.example.revplaycatalogservice.service;

import org.example.revplaycatalogservice.dto.AlbumDTO;
import org.example.revplaycatalogservice.dto.SongDTO;
import org.example.revplaycatalogservice.entity.Album;
import org.example.revplaycatalogservice.entity.Artist;
import org.example.revplaycatalogservice.entity.Song;
import org.example.revplaycatalogservice.exception.ResourceNotFoundException;
import org.example.revplaycatalogservice.repository.AlbumRepository;
import org.example.revplaycatalogservice.repository.ArtistRepository;
import org.example.revplaycatalogservice.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final FileStorageService fileStorageService;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository,
                        SongRepository songRepository, FileStorageService fileStorageService) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.fileStorageService = fileStorageService;
    }

    public AlbumDTO getAlbumById(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        return mapToDTOWithSongs(album);
    }

    public AlbumDTO createAlbum(String email, String title, String description, Integer releaseYear, MultipartFile coverImage) {
        Artist artist = artistRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Artist not found. Please upload a song first to initialize profile."));

        Album album = new Album();
        album.setTitle(title);
        album.setDescription(description);
        album.setArtist(artist);

        if (releaseYear != null) album.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
        if (coverImage != null && !coverImage.isEmpty()) {
            album.setCoverImageUrl(fileStorageService.storeFile(coverImage));
        }

        return mapToDTO(albumRepository.save(album));
    }

    public List<AlbumDTO> getMyAlbums(String email) {
        Artist artist = artistRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        return albumRepository.findByArtist_ArtistId(artist.getArtistId())
                .stream().map(this::mapToDTO).toList();
    }

    public AlbumDTO updateAlbum(Long albumId, String title, String description, Integer releaseYear, MultipartFile coverImage) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        album.setTitle(title);
        album.setDescription(description);

        if (releaseYear != null) album.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
        if (coverImage != null && !coverImage.isEmpty()) {
            album.setCoverImageUrl(fileStorageService.storeFile(coverImage));
        }

        return mapToDTO(albumRepository.save(album));
    }

    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new RuntimeException("Album not found"));
        if (album.getSongs() != null && !album.getSongs().isEmpty()) throw new RuntimeException("Cannot delete album. Songs exist.");
        if (album.getCoverImageUrl() != null) fileStorageService.deleteFile(album.getCoverImageUrl());
        albumRepository.delete(album);
    }

    @Transactional
    public String addSongToAlbum(String email, Long albumId, Long songId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!album.getArtist().getEmail().equals(email) || !song.getArtist().getEmail().equals(email)) {
            throw new RuntimeException("You can only modify your own albums and songs!");
        }

        song.setAlbum(album);
        songRepository.save(song);
        return "Song successfully added to the album!";
    }

    @Transactional
    public String removeSongFromAlbum(String email, Long albumId, Long songId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        Song song = songRepository.findById(songId).orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!album.getArtist().getEmail().equals(email)) throw new RuntimeException("You can only modify your own albums!");

        if (song.getAlbum() != null && song.getAlbum().getAlbumId().equals(albumId)) {
            song.setAlbum(null);
            songRepository.save(song);
        }
        return "Song successfully removed from the album!";
    }

    public List<AlbumDTO> getAllAlbums() {
        return albumRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private AlbumDTO mapToDTO(Album album) {
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getAlbumId());
        dto.setTitle(album.getTitle());
        dto.setDescription(album.getDescription());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverImageUrl(album.getCoverImageUrl());
        if (album.getReleaseDate() != null) dto.setReleaseYear(album.getReleaseDate().getYear());
        return dto;
    }

    private AlbumDTO mapToDTOWithSongs(Album album) {
        AlbumDTO dto = mapToDTO(album);
        if (album.getSongs() != null) {
            dto.setSongs(album.getSongs().stream().map(this::mapSongToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private SongDTO mapSongToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setDuration(song.getDuration());
        dto.setPlayCount(song.getPlayCount());
        dto.setAudioFileUrl(song.getAudioFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        if (song.getArtist() != null) dto.setArtistName(song.getArtist().getArtistName());
        return dto;
    }
}