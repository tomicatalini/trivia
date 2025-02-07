package com.taller.trivia.repository.model;

import java.sql.Date;
import java.util.List;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Game")
public class Game {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private Long score;
    private Date startDate;
    private Date endDate;

    //Relaciones

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private Set set;

    @ManyToMany
    @JoinTable(
        name = "game_questions",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "quiz_question_id")
    )
    private List<QuizQuestion> questions;

    //Constructores
    public Game() {}

    //Getters & Setters
    public Long getId() { return this.id; }
    
    public Long getScore() { return this.score; }
    public void setScore(Long score) { this.score = score; }

    public Date getStarDate() { return this.startDate; }
    public void setStartDate(Date date) { this.startDate = date; }

    public Date getEndDate() { return this.endDate; }
    public void setEndDate(Date date) { this.endDate = date; }
}
