package org.example.revplaycatalogservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long albumId;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @OneToMany(mappedBy = "album")
    private List<Song> songs;

    public Album() {}

    // Getters and Setters
    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }
}