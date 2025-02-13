package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taller.trivia.dto.UserDTO;

@Service
public interface UserService {

    List<UserDTO> getAll();
    Optional<UserDTO> getById(Long id);
    Optional<UserDTO> getByName(String name);
    Optional<UserDTO> getByEmail(String email);
    UserDTO save(UserDTO user);
    UserDTO update(Long userId, UserDTO userDto);
    void delete(Long id);    
    Boolean validateUserPass(UserDTO userDto, String pass);
    UserDTO updatePass(UserDTO userDto, String oldPass, String newPass);
}
