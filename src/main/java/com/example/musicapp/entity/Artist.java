package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_path", length = 255)
    private String avatarPath;

    // Relationship: One Artist can have many Songs
    // 'mappedBy = "artist"' refers to the 'artist' field in the Song entity
    // CascadeType.ALL means operations (persist, remove, merge, refresh, detach) on Artist will cascade to associated Songs
    // FetchType.LAZY means Songs won't be loaded until explicitly requested
    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    private Set<Song> songs;

    // Constructors (optional, Lombok generates NoArgsConstructor)
    public Artist(String name) {
        this.name = name;
    }

    // toString, equals, hashCode (optional, Lombok can generate these too with @ToString, @EqualsAndHashCode)
    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bio='" + (bio != null ? (bio.length() > 30 ? bio.substring(0,30)+"..." : bio) : null) + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                '}';
    }
}
