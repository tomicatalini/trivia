package com.taller.trivia.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String category;
    private String description;
    private Boolean enable;
    private List<QuestionDTO> questions;
}
