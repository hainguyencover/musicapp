package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    // Relationship: Many Songs belong to one Artist
    // @JoinColumn specifies the foreign key column in the 'songs' table
    // FetchType.EAGER (or default) means Artist info is loaded immediately with Song info
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    // Relationship: Many Songs belong to one Genre
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(name = "file_path", nullable = false) // Store the path to the music file
    private String filePath;

    // Constructors (optional)
    public Song(String name, Artist artist, Genre genre, String filePath) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artist=" + (artist != null ? artist.getName() : "null") + // Avoid recursion
                ", genre=" + (genre != null ? genre.getName() : "null") +   // Avoid recursion
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
