package com.taller.trivia.service;

import java.util.Date;
import java.util.List;

import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.RankingDTO;
import com.taller.trivia.dto.ResumeDTO;

public interface GameService {
    GameDTO createGame(Long quizId, Long playerId, Long categoryId, String level, int numberOfQuestions);
    GameDTO getGameById(Long gameId);
    List<GameDTO> getAllGamesByPlayerId(Long playerId);
    ResumeDTO endGame(Long gameId, Date endDate,List<GameQuestionDTO> gamesQuestions);
    List<RankingDTO> findTopRanking(int numberOfGames);
}
