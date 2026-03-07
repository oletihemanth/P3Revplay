package org.example.revplayplaybackservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Data
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "song_id", nullable = false)
    private Long songId;

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "song_duration", nullable = false)
    private Integer songDuration;

    @Column(name = "played_at", updatable = false)
    private LocalDateTime playedAt;

    @PrePersist
    protected void onCreate() {
        this.playedAt = LocalDateTime.now();
    }

    public History() {}

    public History(String userEmail, Long songId, Long playlistId, Integer songDuration) {
        this.userEmail = userEmail;
        this.songId = songId;
        this.playlistId = playlistId;
        this.songDuration = songDuration;
    }
}