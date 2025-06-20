package com.taller.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    long userId;
    String username;
    String email;
    boolean authenticated = false;
    boolean admin = false;
}
