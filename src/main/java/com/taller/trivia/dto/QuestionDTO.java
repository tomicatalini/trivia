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
    private CategoryDTO category;
    private List<AnswerDTO> answers;
    private Integer responseTime;

    public QuestionDTO(Long id, String question, String type, Level level, CategoryDTO category, List<AnswerDTO> answers) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.level = level;
        this.category = category;
        this.answers = answers;
    }
}
