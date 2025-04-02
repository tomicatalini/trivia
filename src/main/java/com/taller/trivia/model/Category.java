package com.taller.trivia.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Category")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40, nullable = false)
    private String title;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private boolean enable;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    public Category(Long id, String title, String description, boolean enable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.enable = enable;
    }

}
