package com.taller.trivia.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column( length = 40, nullable = false)
    private String name;

    @Column( length = 30, nullable = false)
    private String password;

    @Column( length = 50, nullable = false)
    private String email;

    @Column( length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    //Relaciones
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games;
}
