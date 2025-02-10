package com.taller.trivia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.trivia.model.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
