package com.taller.trivia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.trivia.model.Question;

public interface QuestionRepository extends JpaRepository <Question, Long> {

}
