package com.taller.trivia.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDTO {
    private Long id;
    private String answer;
    private Boolean valid;
}
