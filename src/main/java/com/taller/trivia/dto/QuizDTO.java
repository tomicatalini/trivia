package com.taller.trivia.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizDTO {
    private Long id;
    private String name;
    private String url;
    private List<GameDTO> games;
}
