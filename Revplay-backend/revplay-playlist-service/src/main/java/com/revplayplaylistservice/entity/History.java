package com.revplayplaylistservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    //  MICROSERVICE CHANGE: Decoupled User and Song
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "song_id", nullable = false)
    private Long songId;

    // We CAN keep this one because Playlist is in the same database!
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @Column(name = "played_at", updatable = false)
    private LocalDateTime playedAt;

    @PrePersist
    protected void onCreate() {
        this.playedAt = LocalDateTime.now();
    }

    public History(String userEmail, Long songId) {
        this.userEmail = userEmail;
        this.songId = songId;
    }
}