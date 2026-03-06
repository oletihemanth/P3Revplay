package org.example.revplaycatalogservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "liked_songs")
public class LikedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MICROSERVICE MAGIC: We store the email pointer instead of the User entity!
    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public LikedSong() {}

    public LikedSong(String email, Song song) {
        this.email = email;
        this.song = song;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Song getSong() { return song; }
    public void setSong(Song song) { this.song = song; }
}