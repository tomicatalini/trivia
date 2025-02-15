package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taller.trivia.dto.UserDTO;

@Service
public interface QuestionService {
    List<UserDTO> getAll();
    Optional<UserDTO> getById(Long id);
    UserDTO save(UserDTO user, String password);
    UserDTO update(Long userId, UserDTO userDto);
    void delete(Long id);
}
