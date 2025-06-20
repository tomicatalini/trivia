package com.taller.trivia.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String category;
    private String description;
    private boolean enable;
    private Long quizId;
    private List<QuestionDTO> questions;

    public CategoryDTO(Long id, String category, String description, boolean enable, Long quizId) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.enable = enable;
        this.quizId = quizId;
    }   
}
