package com.taller.trivia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.ResumeDTO;
import com.taller.trivia.dto.StartGameRequestDTO;
import com.taller.trivia.service.GameService;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.util.ResponseHandler;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody StartGameRequestDTO startGameRequest) {
        try {

            GameDTO gameDTO = this.gameService.createGame(
                startGameRequest.getQuizId(),
                startGameRequest.getPlayerId(),
                startGameRequest.getCategoryId(),
                startGameRequest.getLevel(),
                startGameRequest.getNumberOfQuestions()
            );
            
            return ResponseHandler.handleResponse(gameDTO);
        } catch (Exception e) {
            System.out.println("GameController error: " + e.getMessage());
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessageLoader.getMessage("SERVER_ERROR"), e.getMessage());
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endGame(@RequestBody GameDTO gameDTO) {
        try {
            ResumeDTO resume = this.gameService.endGame(
                    gameDTO.getGameId(), 
                    gameDTO.getEndDate(), 
                    gameDTO.getGameQuestions()
                );
            
            return ResponseHandler.handleResponse(resume);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessageLoader.getMessage("SERVER_ERROR"), e.getMessage());
        }
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> findUsersTops() {
        try {

            return ResponseHandler.handleResponse(
                this.gameService.findTopRanking(20)
            );
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessageLoader.getMessage("SERVER_ERROR"), e.getMessage());
        }
    }
}
