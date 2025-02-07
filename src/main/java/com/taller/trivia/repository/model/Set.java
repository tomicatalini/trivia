package com.taller.trivia.repository.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table( name = "Set")
public class Set { 

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( length = 100)
    private String name;

    @Column( length = 200)
    private String url;

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games;

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> questions;

    //Constructores
    public Set() {}

    //Getters & Setters
    public Long getId() { return this.id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    
    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

}
