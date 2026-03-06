package org.example.revplaycatalogservice.repository;

import org.example.revplaycatalogservice.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtist_ArtistId(Long artistId);
}