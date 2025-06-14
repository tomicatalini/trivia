package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.LevelDTO;
import com.taller.trivia.dto.QuestionDTO;

public interface QuestionService {

    QuestionDTO save(QuestionDTO quizDTO);
    boolean delete(Long id);
    Optional<QuestionDTO> getById(Long id);
    List<QuestionDTO> getAll();
    List<QuestionDTO> getByCategory(Long id);
    List<QuestionDTO> getQuestionsGame(Long gameId);
    List<GameQuestionDTO> getRandomQuestions(Long quizId, Long categoryId, Long gameId, String level, int numberOfQuestions);

    List<LevelDTO> getQuestionsLevel();
}