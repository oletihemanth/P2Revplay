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


## RevPlay - Music Streaming & Management Platform:

RevPlay is a full-stack, Spotify-inspired music streaming application. It features a robust role-based system that provides distinct experiences for Listeners (who want to discover and organize music) and Artists (who want to upload, manage, and track the performance of their discography).

## Key Features:

## For All Users (Listeners & Artists)
Secure Authentication: JWT-based login and registration system.

Home Dashboard: A personalized feed showing "Recently Played" tracks, "Trending Albums," and a "Discover" section.

Music Discovery: Search for songs by title or filter them by genre (Pop, Rock, Hip-Hop, etc.).

Playlists: Create custom playlists, add/remove songs, and browse public playlists.

Favorites: "Heart" your favorite tracks and access them instantly in your Favorites library.

Public Album Pages: Beautiful, dedicated pages for albums showing cover art, release year, and the full tracklist.

Profile Management: Update and view user profile details.

## For Artists Only (Studio Dashboard)
Role-Based Access: Exclusive routes protected by artistGuard.

Music Uploads: Upload new audio files with custom cover art, genres, and visibility settings.

My Music Library: A dedicated management page for all uploaded tracks.

Album Management: * Create, edit, and delete albums.

Interactive "Manage Album" UI to easily add/remove uploaded tracks to an album's tracklist.

Safety checks (e.g., cannot delete an album if it contains songs).

Analytics Dashboard: Track play counts and engagement for uploaded music.

## Tech Stack
## Frontend
Framework: Angular 20 (Standalone Components)

Routing: Modern Angular Router with nested routes and Route Guards (authGuard, artistGuard).

Styling: Custom CSS with a premium Dark Mode theme (Flexbox, CSS Grid).

HTTP: Angular HttpClient with Interceptors/Headers for JWT attachment.

## Backend
Framework: Java Spring Boot 3.x

Security: Spring Security & JSON Web Tokens (JWT)

Database Management: Spring Data JPA / Hibernate

Architecture: Controller-Service-Repository pattern using DTOs (Data Transfer Objects) for clean API responses.

## Project Structure
## Frontend (Angular)
The frontend follows a feature-based, scalable architecture:

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


## Setup & Installation
## Prerequisites
Node.js (v18+ recommended)

Angular CLI (npm install -g @angular/cli)

Java Development Kit (JDK) (v17+ recommended)

Maven (or Gradle)

MySQL/PostgreSQL (Ensure your local database server is running)

1. Backend Setup (Spring Boot)
Navigate to the backend directory.

2.Open src/main/resources/application.properties and configure your database credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/revplay_db
spring.datasource.username=root
spring.datasource.password=your_password

3.Run the application using your IDE (Eclipse/IntelliJ) or via Maven:
mvn spring-boot:run

4.The backend will start on http://localhost:8080.

## 2. Frontend Setup (Angular)
Navigate to the frontend directory:

Bash
cd frontend
Install the required dependencies:

Bash
npm install
Ensure your src/environments/environment.ts points to your backend URL:

TypeScript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api' // Adjust if needed
};
Start the development server:

Bash
ng serve
Open your browser and navigate to http://localhost:4200.

## Key API Endpoints (Backend)

Method,Endpoint,Description,Role Required
POST,/api/auth/login,Authenticate user & get JWT,None
GET,/api/songs,Get all discoverable songs,User/Listener
POST,/api/songs,Upload a new song,Artist
GET,/api/albums,Get trending albums,User/Listener
POST,/api/albums,Create a new album,Artist
POST,/api/albums/{id}/songs/{songId},Add song to album tracklist,Artist
POST,/api/playlists,Create a new playlist,User/Listener

