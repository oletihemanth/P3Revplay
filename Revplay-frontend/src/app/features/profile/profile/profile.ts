import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth'; 
import { environment } from '../../../../environments/environment'; 

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  userProfile: any = null;
  
  // Initialize with 0s so the HTML doesn't break while loading!
  userStats: any = {
    totalPlaylists: 0,
    favoriteSongsCount: 0,
    totalPlays: 0
  };
  
  newName: string = '';
  isEditing: boolean = false;

  ngOnInit() {
    this.fetchProfile();
    this.fetchStats();
  }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  }

  fetchProfile() {
    this.http.get(`${environment.apiUrl}/users/me`, { headers: this.getHeaders() }).subscribe({
      next: (data: any) => {
        this.userProfile = data;
        this.newName = data.name; 
      },
      error: (err) => console.error('Failed to load profile', err)
    });
  }

  fetchStats() {
    const headers = this.getHeaders();

    // Fetch the REAL favorites count directly from your Favorite Service!
    this.http.get<any>(`${environment.apiUrl}/favorites/count`, { headers }).subscribe({
      next: (data) => this.userStats.favoriteSongsCount = data.count || 0,
      error: (err) => console.error('Failed to load favorites stats', err)
    });

    // Fetch the REAL playlists count directly from your Playlist Service!
    this.http.get<any[]>(`${environment.apiUrl}/playlists/me`, { headers }).subscribe({
      next: (data) => this.userStats.totalPlaylists = data.length || 0,
      error: (err) => console.error('Failed to load playlists stats', err)
    });

    //  FIX: Fetch the REAL total play count for Artists directly from Catalog Service!
    this.http.get<any>(`${environment.apiUrl}/songs/internal/artist/stats`, { headers }).subscribe({
      next: (data) => this.userStats.totalPlays = data.totalPlays || 0,
      error: (err) => console.error('Failed to load total plays', err)
    });
  }

  updateName() {
    if (!this.newName.trim()) {
      alert("Name cannot be empty!");
      return;
    }

    const body = { name: this.newName };

    this.http.put(`${environment.apiUrl}/users/me`, body, { headers: this.getHeaders() })
      .subscribe({
        next: () => {
          this.userProfile.name = this.newName;
          this.isEditing = false;
          // Update local storage so the header updates immediately
          localStorage.setItem('userName', this.newName);
        },
        error: (err) => {
          console.error('Failed to update name', err);
          alert("Failed to update name.");
        }
      });
  }
}