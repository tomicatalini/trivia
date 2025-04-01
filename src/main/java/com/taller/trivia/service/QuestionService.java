package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.dto.QuestionDTO;

public interface QuestionService {

    QuestionDTO save(QuestionDTO quizDTO);
    void delete(Long id);
    Optional<QuestionDTO> getById(Long id);
    List<QuestionDTO> getAll();
    List<QuestionDTO> getByCategory(String name);
    List<QuestionDTO> getQuestionsGame(Long gameId);

}