package com.revplayplaylistservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String privacy = "PUBLIC";

    //  MICROSERVICE CHANGE: Replaced User entity with userEmail string!
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // We link to PlaylistSong to keep track of the songs in this playlist
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistSong> playlistSongs = new ArrayList<>();

    public void addPlaylistSong(PlaylistSong playlistSong) {
        this.playlistSongs.add(playlistSong);
    }
}