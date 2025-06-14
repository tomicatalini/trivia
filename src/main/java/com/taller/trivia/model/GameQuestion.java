package com.taller.trivia.model;

import java.util.Date;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameQuestion {

    @EmbeddedId
    private GameQuestionId id = new GameQuestionId();

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

    private Date start;
    private Date finish;
    private boolean valid; // true si la respuesta es correcta, false si no lo es

    public GameQuestion(Game game, Question question, Date start, Date finish) {
        
        this.id.setGameId(game.getId());
        this.id.setQuestionId(question.getId());
        
        this.question = question;
        this.game = game;
        this.valid = false;
    }      
}
