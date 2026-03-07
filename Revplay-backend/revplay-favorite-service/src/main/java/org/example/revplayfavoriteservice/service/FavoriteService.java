package org.example.revplayfavoriteservice.service;

import org.example.revplayfavoriteservice.client.CatalogClient;
import org.example.revplayfavoriteservice.dto.SongDTO;
import org.example.revplayfavoriteservice.entity.Favorite;
import org.example.revplayfavoriteservice.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CatalogClient catalogClient;

    public FavoriteService(FavoriteRepository favoriteRepository, CatalogClient catalogClient) {
        this.favoriteRepository = favoriteRepository;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public String toggleFavorite(String email, Long songId) {
        Optional<Favorite> existing = favoriteRepository.findByUserEmailAndSongId(email, songId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return "Song removed from favorites.";
        } else {
            favoriteRepository.save(new Favorite(email, songId));
            return "Song added to favorites!";
        }
    }

    public List<SongDTO> getMyFavorites(String email) {
        // Find the favorite IDs, then ask CatalogClient for the full song details!
        return favoriteRepository.findByUserEmail(email).stream()
                .map(favorite -> {
                    try {
                        return catalogClient.getSongById(favorite.getSongId());
                    } catch (Exception e) {
                        return null; // Ignore if the song was deleted from the catalog
                    }
                })
                .filter(song -> song != null)
                .collect(Collectors.toList());
    }

    public long getFavoritesCount(String email) {
        return favoriteRepository.countByUserEmail(email);
    }
}