package org.example.revplaycatalogservice.dto;

public class SongDTO {
    private Long songId;
    private String title;
    private String genre;
    private Integer duration;
    private Long playCount;
    private String audioFileUrl;
    private String coverImageUrl;
    private String artistName;
    private String albumName;

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Long getPlayCount() { return playCount; }
    public void setPlayCount(Long playCount) { this.playCount = playCount; }

    public String getAudioFileUrl() { return audioFileUrl; }
    public void setAudioFileUrl(String audioFileUrl) { this.audioFileUrl = audioFileUrl; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getAlbumName() { return albumName; }
    public void setAlbumName(String albumName) { this.albumName = albumName; }
}