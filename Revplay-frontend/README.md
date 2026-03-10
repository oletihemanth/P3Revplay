<!-- # Frontend

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.1.4.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page. -->


# RevPlay - Music Streaming & Management Platform (Microservices Architecture)

RevPlay is a full-stack, Spotify-inspired music streaming application. It features a robust role-based system that provides distinct experiences for Listeners (who want to discover and organize music) and Artists (who want to upload, manage, and track the performance of their discography). 

In its latest iteration, the backend has been completely re-architected into a highly scalable **Spring Cloud Microservices ecosystem**.

## Key Features

### For All Users (Listeners & Artists)
* **Secure Authentication:** JWT-based login and registration system.
* **Home Dashboard:** A personalized feed showing "Recently Played" tracks, "Trending Albums," and a "Discover" section.
* **Music Discovery:** Search for songs by title or filter them by genre (Pop, Rock, Hip-Hop, etc.).
* **Playlists:** Create custom playlists, add/remove songs, and browse public playlists.
* **Favorites:** "Heart" your favorite tracks and access them instantly in your Favorites library.
* **Public Album Pages:** Beautiful, dedicated pages for albums showing cover art, release year, and the full tracklist.
* **Profile Management:** Update and view user profile details.

### For Artists Only (Studio Dashboard)
* **Role-Based Access:** Exclusive routes protected by `artistGuard`.
* **Music Uploads:** Upload new audio files with custom cover art, genres, and visibility settings.
* **My Music Library:** A dedicated management page for all uploaded tracks.
* **Album Management:** Create, edit, and delete albums. Interactive UI to easily add/remove uploaded tracks to an album's tracklist.
* **Analytics Dashboard:** Track play counts and engagement for uploaded music.

---

## Tech Stack

### Frontend
* **Framework:** Angular 21 (Standalone Components)
* **Routing:** Modern Angular Router with nested routes and Route Guards (`authGuard`, `artistGuard`).
* **Styling:** Custom CSS with a premium Dark Mode theme (Flexbox, CSS Grid).
* **HTTP:** Angular HttpClient with Interceptors/Headers for JWT attachment.

### Backend (Microservices)
* **Framework:** Java Spring Boot 4.0.x
* **Cloud Native:** Spring Cloud 2025.1.0
* **Routing & Discovery:** Spring Cloud Gateway, Netflix Eureka
* **Centralized Config:** Spring Cloud Config Server
* **Inter-Service Communication:** OpenFeign
* **Fault Tolerance:** Resilience4j (Circuit Breakers)
* **Security:** Spring Security & JSON Web Tokens (JWT)
* **Database Management:** Spring Data JPA / Hibernate with MySQL

---

## Project Structure



### Backend (Microservices)
The backend is broken down into independent, scalable services:
* `revplay-config-server` (Port 8888): Centralized configuration management.
* `revplay-eureka-server` (Port 8761): Service registry and discovery.
* `revplay-api-gateway` (Port 8080): Central entry point, routing, and global Swagger UI aggregator.
* `revplay-auth-service`: Handles JWT generation, login, and registration.
* `revplay-catalog-service`: Manages Songs, Albums, and file storage.
* `revplay-playlist-service`: Manages Playlists, Favorites, and User Follows.
* `revplay-analytics-service`: Tracks play history and artist metrics.

### Frontend (Angular)
The frontend follows a feature-based, scalable architecture:
```text
src/app/
├── core/                  # Singleton services, guards, and interceptors
│   ├── guards/            # authGuard, artistGuard
│   └── services/          # AuthService, SongService, AlbumService, PlaylistService
├── features/              # Smart components organized by feature domain
│   ├── albums/            # MyAlbums, ManageAlbum, AlbumDetail
│   ├── auth/              # Login, Register
│   ├── home/              # Main Dashboard
│   ├── music/             # UploadSong, MyMusic
│   ├── playlists/         # Playlists, PlaylistDetail, DiscoverPlaylists
│   └── profile/           # User Profile & Settings
└── shared/                # Dumb components, pipes, and shared UI models


Gemini said
While I cannot directly send a downloadable file attachment through this chat window, I have formatted the entire README.md perfectly in the raw code block below.

All you have to do is click the "Copy" button in the top right corner of this block and paste it directly into your README.md file in your IDE!

Markdown
# RevPlay - Music Streaming & Management Platform (Microservices Architecture)

RevPlay is a full-stack, Spotify-inspired music streaming application. It features a robust role-based system that provides distinct experiences for Listeners (who want to discover and organize music) and Artists (who want to upload, manage, and track the performance of their discography). 

In its latest iteration, the backend has been completely re-architected into a highly scalable **Spring Cloud Microservices ecosystem**.

Key Features

### For All Users (Listeners & Artists)
* **Secure Authentication:** JWT-based login and registration system.
* **Home Dashboard:** A personalized feed showing "Recently Played" tracks, "Trending Albums," and a "Discover" section.
* **Music Discovery:** Search for songs by title or filter them by genre (Pop, Rock, Hip-Hop, etc.).
* **Playlists:** Create custom playlists, add/remove songs, and browse public playlists.
* **Favorites:** "Heart" your favorite tracks and access them instantly in your Favorites library.
* **Public Album Pages:** Beautiful, dedicated pages for albums showing cover art, release year, and the full tracklist.
* **Profile Management:** Update and view user profile details.

### For Artists Only (Studio Dashboard)
* **Role-Based Access:** Exclusive routes protected by `artistGuard`.
* **Music Uploads:** Upload new audio files with custom cover art, genres, and visibility settings.
* **My Music Library:** A dedicated management page for all uploaded tracks.
* **Album Management:** Create, edit, and delete albums. Interactive UI to easily add/remove uploaded tracks to an album's tracklist.
* **Analytics Dashboard:** Track play counts and engagement for uploaded music.

---

## Tech Stack

### Frontend
* **Framework:** Angular 21 (Standalone Components)
* **Routing:** Modern Angular Router with nested routes and Route Guards (`authGuard`, `artistGuard`).
* **Styling:** Custom CSS with a premium Dark Mode theme (Flexbox, CSS Grid).
* **HTTP:** Angular HttpClient with Interceptors/Headers for JWT attachment.

### Backend (Microservices)
* **Framework:** Java Spring Boot 4.0.x
* **Cloud Native:** Spring Cloud 2025.1.0
* **Routing & Discovery:** Spring Cloud Gateway, Netflix Eureka
* **Centralized Config:** Spring Cloud Config Server
* **Inter-Service Communication:** OpenFeign
* **Fault Tolerance:** Resilience4j (Circuit Breakers)
* **Security:** Spring Security & JSON Web Tokens (JWT)
* **Database Management:** Spring Data JPA / Hibernate with MySQL

---

## Project Structure



### Backend (Microservices)
The backend is broken down into independent, scalable services:
* `revplay-config-server` (Port 8888): Centralized configuration management.
* `revplay-eureka-server` (Port 8761): Service registry and discovery.
* `revplay-api-gateway` (Port 8080): Central entry point, routing, and global Swagger UI aggregator.
* `revplay-auth-service`: Handles JWT generation, login, and registration.
* `revplay-catalog-service`: Manages Songs, Albums, and file storage.
* `revplay-playlist-service`: Manages Playlists, Favorites, and User Follows.
* `revplay-analytics-service`: Tracks play history and artist metrics.

### Frontend (Angular)
The frontend follows a feature-based, scalable architecture:
```text
src/app/
├── core/                  # Singleton services, guards, and interceptors
│   ├── guards/            # authGuard, artistGuard
│   └── services/          # AuthService, SongService, AlbumService, PlaylistService
├── features/              # Smart components organized by feature domain
│   ├── albums/            # MyAlbums, ManageAlbum, AlbumDetail
│   ├── auth/              # Login, Register
│   ├── home/              # Main Dashboard
│   ├── music/             # UploadSong, MyMusic
│   ├── playlists/         # Playlists, PlaylistDetail, DiscoverPlaylists
│   └── profile/           # User Profile & Settings
└── shared/                # Dumb components, pipes, and shared UI models
Setup & Installation
Prerequisites
Node.js (v18+ recommended)

Angular CLI (npm install -g @angular/cli)

Java Development Kit (JDK) (v17/21 recommended)

Maven

MySQL (Ensure your local database server is running)

1. Backend Setup (Spring Boot Microservices)
Because this is a microservices architecture, the startup order is strictly important.

Database: Ensure MySQL is running and create the necessary databases (e.g., revplay_auth_db, revplay_catalog_db, etc., as defined in your configs).

Start the Infrastructure Services (In Order):

Run revplay-config-server first (Wait for it to start on 8888).

Run revplay-eureka-server second (Wait for it to start on 8761).

Start the Core Microservices:

Run revplay-auth-service

Run revplay-catalog-service

Run revplay-playlist-service

Run revplay-analytics-service

Start the API Gateway:

Run revplay-api-gateway (This will bind to http://localhost:8080).

Verify all services are registered by visiting the Eureka Dashboard at http://localhost:8761.

2. Frontend Setup (Angular)
Navigate to the frontend directory:

Bash
cd frontend
npm install
Ensure your src/environments/environment.ts points to your API Gateway URL:

TypeScript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api' // Points to the Gateway, NOT individual services
};
Start the development server:

Bash
ng serve
Open your browser and navigate to http://localhost:4200/.

Key API Endpoints (Via API Gateway)
All frontend requests flow through the API Gateway (localhost:8080), which routes them to the correct microservice.


Method,Endpoint,Routed To Service,Description,Role Required
POST,/api/auth/login,auth-service,Authenticate user & get JWT,None
POST,/api/auth/register,auth-service,Register a new user,None
GET,/api/songs,catalog-service,Get all discoverable songs,User/Listener
POST,/api/songs,catalog-service,Upload a new song,Artist
GET,/api/albums,catalog-service,Get trending albums,User/Listener
POST,/api/albums/{id}/songs/{songId},catalog-service,Add song to album tracklist,Artist
POST,/api/playlists,playlist-service,Create a new playlist,User/Listener
GET,/api/history/me,analytics-service,Get user's listening history,User/Listener