package com.taller.trivia.dto;

import java.util.ArrayList;
import java.util.List;

import com.taller.trivia.model.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Rol rol;
    private List<GameDTO> games;

    public UserDTO(Long id, String name, String email, Rol rol) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rol = rol;
        this.games = new ArrayList<>();
    }
}
