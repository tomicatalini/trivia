package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.dto.AuthDTO;
import com.taller.trivia.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAll();
    Optional<UserDTO> getById(Long id);
    List<UserDTO> getByName(String name);
    List<UserDTO> getByEmail(String email);
    UserDTO save(UserDTO user, String password);
    UserDTO update(Long userId, UserDTO userDto);
    boolean delete(Long id);    
    AuthDTO validateUserPass(String username, String password);
    boolean updatePass(String username, String oldPass, String newPass);
}
