import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Playlist } from '../../../core/services/playlist/playlist'; 
import { AudioService } from '../../../core/services/audio/audio'; 
import { Song } from '../../../core/services/song/song'; 
import { History } from '../../../core/services/history/history'; 
import { environment } from '../../../../environments/environment'; 

@Component({
  selector: 'app-playlist-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './playlist-detail.html',
  styleUrl: './playlist-detail.css'
})
export class PlaylistDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private playlistService = inject(Playlist);
  
  // Inject Global Player Services
  private audioService = inject(AudioService);
  private songService = inject(Song);
  private historyService = inject(History);

  playlistId: number = 0;
  playlistData: any = null;

  ngOnInit() {
    this.playlistId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPlaylist();
  }

  loadPlaylist() {
    this.playlistService.getPlaylistById(this.playlistId).subscribe({
      next: (data: any) => {
        this.playlistData = data;
      },
      error: (err: any) => console.error('Failed to load playlist', err)
    });
  }

  // Send to Global Player
  playSong(song: any) {
    const mappedQueue = this.playlistData.songs.map((s: any) => ({
      songId: s.songId,
      title: s.title || s.songTitle,
      artistName: s.artistName || 'Unknown Artist',
      audioFileUrl: s.audioFileUrl || null,
      coverImageUrl: s.coverImageUrl || this.playlistData.coverImageUrl || null 
    }));

    const songToPlay = mappedQueue.find((s: any) => s.songId === song.songId);

    if (songToPlay) {
      this.audioService.playSong(songToPlay, mappedQueue);

      this.songService.incrementPlayCount(song.songId).subscribe({
        error: (err: any) => console.error('Failed to update play count:', err)
      });

      this.historyService.logPlay(song.songId).subscribe({
        error: (err: any) => console.error('Failed to log history:', err)
      });
    }
  }

  // Plays the full playlist starting from track 1
  playFullPlaylist() {
    if (this.playlistData?.songs?.length > 0) {
      this.playSong(this.playlistData.songs[0]); 
    }
  }

  removeSong(event: Event, songId: number) {
    event.stopPropagation(); 
    
    if(confirm('Remove this song from your playlist?')) {
      this.playlistService.removeSongFromPlaylist(this.playlistId, songId).subscribe({
        next: () => {
          this.playlistData.songs = this.playlistData.songs.filter((s: any) => s.songId !== songId);
        },
        error: (err: any) => console.error('Failed to remove song', err)
      });
    }
  }

  //  FIX: Smart URL builder to route playlist images and song images to their correct microservices!
  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return '';
    
    // Check if the image matches the playlist's main cover image
    if (this.playlistData && this.playlistData.coverImageUrl === fileName) {
      return `${environment.apiUrl}/playlists/image/${fileName}`;
    }
    
    // Otherwise, it must be a song cover inside the playlist
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }

  onImageError(event: any) {
    event.target.style.display = 'none';
  }
}