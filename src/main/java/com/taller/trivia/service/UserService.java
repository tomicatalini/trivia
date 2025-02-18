package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAll();
    Optional<UserDTO> getById(Long id);
    Optional<UserDTO> getByName(String name);
    Optional<UserDTO> getByEmail(String email);
    UserDTO save(UserDTO user, String password);
    UserDTO update(Long userId, UserDTO userDto);
    boolean delete(Long id);    
    boolean validateUserPass(UserDTO userDto, String pass);
    UserDTO updatePass(UserDTO userDto, String oldPass, String newPass);
}
