package com.taller.trivia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.trivia.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
