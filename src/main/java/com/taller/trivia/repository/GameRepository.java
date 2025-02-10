package com.taller.trivia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.trivia.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

}
