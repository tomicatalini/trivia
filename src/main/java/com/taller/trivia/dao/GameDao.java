package com.taller.trivia.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.taller.trivia.model.Game;

public interface GameDao {
    Game save(Game game);
    boolean delete(Long id);
    Optional<Game> findById(Long id);
    List<Game> findAll();    
    List<Game> findAllUserGames(Long userId);
    List<Game> findTopRanking(int number);
    List<Game> findByDates(Date starDate, Date endDate);
    List<Game> findByQuiz(Long quizId);
}
