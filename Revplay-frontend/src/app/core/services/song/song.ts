import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'; //  NEW: Imported to map the backend response!
import { environment } from '../../../../environments/environment';
import { AuthService } from '../auth/auth'; 

@Injectable({
  providedIn: 'root'
})
export class Song {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  
  private apiUrl = `${environment.apiUrl}/songs`;
  private favoritesUrl = `${environment.apiUrl}/favorites`; //  NEW: Pointing to the Favorite Service!

  getAllSongs(): Observable<any[]> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(this.apiUrl, { headers });
  }

  uploadSong(songData: FormData): Observable<any> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(this.apiUrl, songData, { headers });
  }

  searchSongsByTitle(title: string): Observable<any[]> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/search?title=${title}`, { headers });
  }

  filterSongsByGenre(genre: string): Observable<any[]> {
    const token = this.authService.getToken(); 
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/filter?genre=${genre}`, { headers });
  }

  //  FIX: Re-routed to the Favorite Service and mapped the string response to a boolean!
  toggleLike(songId: number): Observable<boolean> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post<any>(`${this.favoritesUrl}/${songId}`, {}, { headers }).pipe(
      map(response => response.message === "Song added to favorites!")
    );
  }

  //  FIX: Re-routed to the Favorite Service!
  getLikedSongs(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(this.favoritesUrl, { headers });
  }

  getMyUploadedSongs(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any[]>(`${this.apiUrl}/my-songs`, { headers });
  }

  deleteSong(songId: number): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.delete(`${this.apiUrl}/${songId}`, { headers, responseType: 'text' as 'json' });
  }

  updateSong(songId: number, title: string, genre: string, visibility: string): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const url = `${this.apiUrl}/${songId}?title=${encodeURIComponent(title)}&genre=${encodeURIComponent(genre)}&visibility=${encodeURIComponent(visibility)}`;
    return this.http.put(url, {}, { headers });
  }

  incrementPlayCount(songId: number): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    //  FIX: Changed .post to .put to match the Controller and the Bouncer!
    return this.http.put(`${this.apiUrl}/${songId}/increment-play`, {}, { headers, responseType: 'text' as 'json' });
  }
}