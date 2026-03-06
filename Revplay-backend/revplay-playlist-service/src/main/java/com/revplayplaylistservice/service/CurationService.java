package com.revplayplaylistservice.service;

import com.revplayplaylistservice.client.CatalogClient;
import com.revplayplaylistservice.client.UserClient;
import com.revplayplaylistservice.dto.PlaylistResponse;
import com.revplayplaylistservice.dto.SongDTO;
import com.revplayplaylistservice.dto.UserDTO;
import com.revplayplaylistservice.entity.*;
import com.revplayplaylistservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurationService {

    private final FavoriteRepository favoriteRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final FollowedPlaylistRepository followedPlaylistRepository;
    private final FileStorageService fileStorageService;

    //  MICROSERVICE MAGIC: Injected Feign Clients instead of Repositories!
    private final UserClient userClient;
    private final CatalogClient catalogClient;

    public CurationService(FavoriteRepository favoriteRepository, PlaylistRepository playlistRepository,
                           PlaylistSongRepository playlistSongRepository, FollowedPlaylistRepository followedPlaylistRepository,
                           FileStorageService fileStorageService, UserClient userClient, CatalogClient catalogClient) {
        this.favoriteRepository = favoriteRepository;
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.followedPlaylistRepository = followedPlaylistRepository;
        this.fileStorageService = fileStorageService;
        this.userClient = userClient;
        this.catalogClient = catalogClient;
    }

    // --- 1. FAVORITES LOGIC ---
    @Transactional
    public String toggleFavorite(String email, Long songId) {
        // We use email and songId directly now!
        Optional<Favorite> existing = favoriteRepository.findByUserEmailAndSongId(email, songId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return "Song removed from favorites.";
        } else {
            Favorite favorite = new Favorite(email, songId);
            favoriteRepository.save(favorite);
            return "Song added to favorites!";
        }
    }

    public List<SongDTO> getMyFavorites(String email) {
        // Fetch the list of Favorite entities, extract the songIds, and ask CatalogClient for the song data!
        return favoriteRepository.findByUserEmail(email).stream()
                .map(favorite -> catalogClient.getSongById(favorite.getSongId()))
                .collect(Collectors.toList());
    }

    public long getFavoritesCount(String email) {
        return favoriteRepository.countByUserEmail(email);
    }

    // --- 2. PLAYLIST CRUD LOGIC ---
    public PlaylistResponse getPlaylistById(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        PlaylistResponse response = mapToResponse(playlist);

        // Fetch the songs mapped to this playlist
        List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistOrderBySongOrderAsc(playlist);

        //  MICROSERVICE MAGIC: Loop through the IDs and fetch real song data from Catalog Service!
        List<SongDTO> songDTOs = new ArrayList<>();
        for (PlaylistSong ps : playlistSongs) {
            try {
                SongDTO song = catalogClient.getSongById(ps.getSongId());
                songDTOs.add(song);
            } catch (Exception e) {
                System.out.println("Warning: Song ID " + ps.getSongId() + " could not be fetched from Catalog Service.");
            }
        }

        response.setSongs(songDTOs);
        return response;
    }

    @Transactional
    public PlaylistResponse createPlaylist(String email, String name, String description, String privacy, MultipartFile coverImage) {
        Playlist playlist = new Playlist();
        playlist.setUserEmail(email); // Saved directly!
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPrivacy(privacy != null ? privacy : "PUBLIC");

        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageName = fileStorageService.storeFile(coverImage);
            playlist.setCoverImageUrl(coverImageName);
        }

        return mapToResponse(playlistRepository.save(playlist));
    }

    @Transactional
    public PlaylistResponse updatePlaylist(String email, Long playlistId, String name, String description, String privacy, MultipartFile coverImage) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPrivacy(privacy);

        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageName = fileStorageService.storeFile(coverImage);
            playlist.setCoverImageUrl(coverImageName);
        }

        return mapToResponse(playlistRepository.save(playlist));
    }

    @Transactional
    public String deletePlaylist(String email, Long playlistId) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);

        if (playlist.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(playlist.getCoverImageUrl());
        }

        playlistRepository.delete(playlist);
        return "Playlist deleted successfully.";
    }

    public List<PlaylistResponse> getMyPlaylists(String email) {
        return playlistRepository.findByUserEmail(email)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<PlaylistResponse> getPublicPlaylists() {
        return playlistRepository.findByPrivacy("PUBLIC")
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- 3. PLAYLIST SONGS LOGIC ---
    @Transactional
    public String addSongToPlaylist(String email, Long playlistId, Long songId) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);

        if (playlistSongRepository.findByPlaylistAndSongId(playlist, songId).isPresent()) {
            return "Song is already in this playlist.";
        }

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSongId(songId);
        ps.setSongOrder(1); // Ideally, calculate max order here.

        playlistSongRepository.save(ps);
        return "Song added to playlist!";
    }

    @Transactional
    public String removeSongFromPlaylist(String email, Long playlistId, Long songId) {
        Playlist playlist = getPlaylistAsOwner(email, playlistId);

        PlaylistSong ps = playlistSongRepository.findByPlaylistAndSongId(playlist, songId)
                .orElseThrow(() -> new RuntimeException("Song not found in this playlist"));

        playlistSongRepository.delete(ps);
        return "Song removed from playlist.";
    }

    // --- 4. FOLLOW / UNFOLLOW PLAYLIST LOGIC ---
    @Transactional
    public String toggleFollowPlaylist(String email, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if ("PRIVATE".equals(playlist.getPrivacy()) && !playlist.getUserEmail().equals(email)) {
            throw new RuntimeException("Cannot follow a private playlist.");
        }

        Optional<FollowedPlaylist> existing = followedPlaylistRepository.findByUserEmailAndPlaylist(email, playlist);

        if (existing.isPresent()) {
            followedPlaylistRepository.delete(existing.get());
            return "Unfollowed playlist.";
        } else {
            FollowedPlaylist follow = new FollowedPlaylist();
            follow.setUserEmail(email);
            follow.setPlaylist(playlist);
            followedPlaylistRepository.save(follow);
            return "Successfully followed playlist!";
        }
    }

    // --- HELPER METHODS ---
    private Playlist getPlaylistAsOwner(String email, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUserEmail().equals(email)) {
            throw new RuntimeException("Unauthorized: You do not own this playlist");
        }
        return playlist;
    }

    private PlaylistResponse mapToResponse(Playlist playlist) {
        PlaylistResponse response = new PlaylistResponse();
        response.setPlaylistId(playlist.getPlaylistId());
        response.setName(playlist.getName());
        response.setDescription(playlist.getDescription());
        response.setPrivacy(playlist.getPrivacy());
        response.setCoverImageUrl(playlist.getCoverImageUrl());

        //  Fetch the creator's real name using the UserClient!
        try {
            UserDTO user = userClient.getUserByEmail(playlist.getUserEmail());
            response.setCreatorName(user.getName());
        } catch (Exception e) {
            response.setCreatorName("Unknown User");
        }

        return response;
    }
}