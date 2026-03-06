package com.revplayplaylistservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlist_songs")
@IdClass(PlaylistSongId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSong {

    @Id
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    //  MICROSERVICE CHANGE: Replaced Song entity with a simple ID!
    @Id
    @Column(name = "song_id", nullable = false)
    private Long songId;

    @Column(name = "song_order", nullable = false)
    private Integer songOrder;
}