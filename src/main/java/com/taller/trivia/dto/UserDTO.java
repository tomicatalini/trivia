package com.taller.trivia.dto;

import java.util.List;

import com.taller.trivia.model.Rol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Rol rol;
    private List<GameDTO> games;
}
