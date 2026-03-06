package com.revplayplaylistservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongId implements Serializable {
    private Long playlist; // Matches the property name 'playlist' in PlaylistSong
    private Long songId;   // Matches the property name 'songId' in PlaylistSong
}