package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.model.Question;

public interface QuestionDao {
    
    Question save(Question question);
    boolean delete(Long id);
    Optional<Question> findById(Long id);
    List<Question> findAll();    
    List<Question> findAllByCategory(Long id);       
    List<Question> findAllQuestionsGame(Long gameId);
}
