package com.taller.trivia.dao;

import java.util.Optional;

import com.taller.trivia.model.Answer;

public interface AnswerDao {

    Answer save(Answer answer);
    void delete(Long id);
    Optional<Answer> findById(Long id);

}
