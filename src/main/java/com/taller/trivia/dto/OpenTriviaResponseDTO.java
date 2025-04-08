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
public class OpenTriviaResponseDTO {
    private int response_code;
    private List<OpenTriviaQuestionDTO> results;
}
