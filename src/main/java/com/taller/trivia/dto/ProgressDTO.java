package com.taller.trivia.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {
    private Long questionId;
    private Date start;
    private Date finish;
    private boolean valid; // true si la respuesta es correcta, false si no lo es
}
