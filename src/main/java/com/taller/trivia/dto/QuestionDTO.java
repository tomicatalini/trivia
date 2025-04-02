package com.taller.trivia.dto;

import java.util.List;

import com.taller.trivia.model.Level;
import com.taller.trivia.model.Category;

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
    private Category category;
    private List<AnswerDTO> answers;
}
