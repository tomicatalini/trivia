package com.taller.trivia.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private long gameId;
    private Date startDate;
    private Date endDate;
    private String mode;
    private int numberOfQuestions;
    private long score;
    private long time;
    private long userId;
    private long quizId;
    private List<GameQuestionDTO> gameQuestions;
}
