package org.example.revplaycatalogservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long artistId;

    // MICROSERVICE MAGIC: We just store the email pointer!
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // We cache the name here so we can search by it without asking the User Service!
    @Column(name = "artist_name")
    private String artistName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 50)
    private String genre;

    @Column(name = "social_links", columnDefinition = "TEXT")
    private String socialLinks;

    @Column(name = "banner_image_url")
    private String bannerImageUrl;

    public Artist() {}

    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }
}