package com.taller.trivia.dto;

import java.util.List;

import com.taller.trivia.model.Level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String question;
    private String type; //Multiple choice, true or false
    private Level level;
    private QuizDTO quiz;
    private List<AnswerDTO> answers;
}
