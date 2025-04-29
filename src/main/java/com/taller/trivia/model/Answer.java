package com.taller.trivia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answer;
    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Answer(String answer, boolean valid, Question question) {
        this.answer = answer;
        this.valid = valid;
        this.question = question;
    }

    public Answer(Long id, String answer, boolean valid) {
        this.id = id;
        this.answer = answer;
        this.valid = valid;
    }
}
