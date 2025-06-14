package com.taller.trivia.dao;
import java.util.Optional;
import java.util.List;

import com.taller.trivia.model.Quiz;

public interface QuizDao {
    Optional<Quiz> findById(Long id);
    List<Quiz> findAll();
    Quiz save(Quiz quiz);
    Quiz update(Long quizId, Quiz quiz);    
    boolean delete(Long id);
    Optional<Quiz> findByName(String name);
}
