package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.dto.QuizDTO;

public interface QuizService {
   
    List<QuizDTO> getAll();
    Optional<QuizDTO> getById(Long id);
    QuizDTO save(QuizDTO quizDTO);
    boolean delete(Long id);
    QuizDTO getByName(String quizName);
}