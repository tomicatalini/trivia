package com.taller.trivia.service;

import java.util.Date;
import java.util.List;

import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;

public interface GameService {
    GameDTO createGame(Long quizId, Long playerId, Long categoryId, String level, int numberOfQuestions);
    GameDTO getGameById(Long gameId);
    List<GameDTO> getAllGamesByPlayerId(Long playerId);
    void endGame(Long gameId, Date endDate,List<GameQuestionDTO> gamesQuestions);
    void saveGameQuestions(Long gameId, List<GameQuestionDTO> gameQuestions);
}
