package com.example.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    // Một Genre có nhiều bài hát (Song)
    @OneToMany(mappedBy = "genre")
    private Set<Song> songs;
}
