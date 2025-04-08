package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taller.trivia.dto.QuizDTO;

@Service
public interface QuizService {
   
    List<QuizDTO> getAll();
    Optional<QuizDTO> getById(Long id);
    QuizDTO save(QuizDTO quizDTO);
    boolean delete(Long id);
    
}