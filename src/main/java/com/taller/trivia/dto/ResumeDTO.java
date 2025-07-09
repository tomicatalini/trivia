package com.taller.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResumeDTO {
    long gameId;
    String mode;
    int numberOfQuestions;
    double score;
    double time;
}
