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
public class Artist extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    // Một Artist có nhiều bài hát (Song)
    @OneToMany(mappedBy = "artist")
    private Set<Song> songs;
}
