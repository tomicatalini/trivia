package com.taller.trivia.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameQuestionDTO {
    //Relación
    private GameDTO game;
    private QuestionDTO question;

    //Datos extras
    private Date start;
    private Date finish;
    private boolean valid;
}
