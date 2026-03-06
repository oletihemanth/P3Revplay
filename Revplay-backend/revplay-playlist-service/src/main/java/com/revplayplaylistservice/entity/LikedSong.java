package com.revplayplaylistservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "liked_songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  MICROSERVICE CHANGE: Replaced User and Song entities!
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "song_id", nullable = false)
    private Long songId;

    public LikedSong(String userEmail, Long songId) {
        this.userEmail = userEmail;
        this.songId = songId;
    }
}