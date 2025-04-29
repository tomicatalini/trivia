package com.taller.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenTriviaCategoryCountResponseDTO {
    private int category_id;
    private OpenTriviaQuestionCountResponseDTO category_question_count;
}
