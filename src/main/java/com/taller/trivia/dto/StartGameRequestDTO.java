package com.taller.trivia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartGameRequestDTO {
    private Long quizId;
    private Long playerId;
    private Long categoryId;
    private String level;
    private int numberOfQuestions;
}
