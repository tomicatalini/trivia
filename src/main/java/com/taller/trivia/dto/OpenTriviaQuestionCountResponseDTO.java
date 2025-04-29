package com.taller.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenTriviaQuestionCountResponseDTO {
    private int total_question_count;
    private int total_easy_question_count;
    private int total_medium_question_count;
    private int total_hard_question_count;   
}
