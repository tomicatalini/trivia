package com.taller.trivia.repository.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column( length = 40, nullable = false)
    private String name;

    @Column( length = 30, nullable = false)
    private String password;

    @Column( length = 50, nullable = false)
    private String mail;

    @Column( length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    //Relaciones
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games;

    //Constructores
    public User() {
        this.rol = Rol.PLAYER;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.rol = Rol.PLAYER;
    }

    //Getters & Setters
    public Long getId() { return this.id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name;}

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password;}

    public String getMail() { return this.mail; }
    public void setMail(String mail) { this.mail = mail; }
    
    public Rol getRol() { return this.rol; }
    public void setRol(String rol) { this.rol = Rol.valueOf(rol); }
}
