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

    @Column( nullable = false)
    private String password;

    private String email;

    @Column( length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    //Relaciones
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games;

    public User(Long id, String name, String email, Rol rol, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rol = rol;
        this.password = password;
    }
}
