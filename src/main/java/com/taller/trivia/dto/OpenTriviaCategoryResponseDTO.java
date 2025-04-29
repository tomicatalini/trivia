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
public class OpenTriviaCategoryResponseDTO {
    private List<OpenTriviaCategoryDTO> trivia_categories;
}

