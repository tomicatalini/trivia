package com.taller.trivia.service;

import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.ProgressDTO;

public interface GameService {
    void createGame(Long quizId, Long playerId, Long categoryId, int numberOfQuestions);
    GameDTO getGameById(Long gameId);
    void getAllGamesByPlayerId(Long playerId);
    void startGame(Long gameId, Long playerId);
    void endGame(Long gameId);
    void saveGameProgress(Long gameId, Long playerId, ProgressDTO progress);
}
