package com.taller.trivia.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.trivia.dao.GameDao;
import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dao.QuizDao;
import com.taller.trivia.dao.UserDao;
import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.RankingDTO;
import com.taller.trivia.model.Game;
import com.taller.trivia.model.GameMode;
import com.taller.trivia.model.GameQuestion;
import com.taller.trivia.model.Question;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.model.User;
import com.taller.trivia.service.GameService;
import com.taller.trivia.util.DTOMapper;

@Service
public class GameServiceImpl implements GameService {


    @Autowired
    private GameDao gameDao;

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Override
    public GameDTO createGame(Long quizId, Long userId, Long categoryId, String gameMode, int numberOfQuestions) {
        if (quizId == null || userId == null) {
            throw new IllegalArgumentException("El ID del cuestionario y el ID del jugador no pueden ser nulos.");
        }

        if (numberOfQuestions <= 0) {
            throw new IllegalArgumentException("El número de preguntas debe ser mayor que 0.");
        }

        Quiz quiz = this.quizDao.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado con ID: " + quizId));
        User user = this.userDao.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        Game game = new Game();
        game.setStartDate(new Date());
        game.setEndDate(null);
        game.setMode(GameMode.valueOf(gameMode.toUpperCase()));
        game.setNumberOfQuestions(numberOfQuestions);
        game.setQuiz(quiz);
        game.setUser(user);
        game.setScore(0L);
    
        List<Question> questions = this.questionDao.findRandomQuestions(quizId, categoryId , gameMode, numberOfQuestions);

        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron preguntas para el juego con ID: " + game.getId());            
        }

        List<GameQuestion> gameQuestions = new ArrayList<GameQuestion>();

        for (Question question : questions) {
            GameQuestion gameQuestion = new GameQuestion(game,question,null,null,false);
            gameQuestions.add(gameQuestion);
        }

        game.setGameQuestions(gameQuestions);
        game = this.gameDao.save(game);

        return DTOMapper.toGameDTO(game);
    }

    @Override
    public GameDTO getGameById(Long gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("El ID del juego no puede ser nulo.");
        }

        Game game = gameDao.findById(gameId).orElse(null);

        if (game == null) {
            throw new IllegalArgumentException("Juego no encontrado con ID: " + gameId);
        }
        
        return DTOMapper.toGameDTO(game);
    }

    @Override
    public List<GameDTO> getAllGamesByPlayerId(Long playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("El ID del jugador no puede ser nulo.");
        }

        List<Game> games = gameDao.findAllUserGames(playerId);

        if (games == null || games.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron juegos para el jugador con ID: " + playerId);
        }

        // Convertir a DTOs y devolver la lista de juegos
        return games.stream()
                .map(DTOMapper::toGameDTO)
                .toList();

    } 

    @Override
    public Long endGame(Long gameId, Date endDate, List<GameQuestionDTO> gameQuestions) {
        GameDTO gameDTO = this.getGameById(gameId);

        if (gameDTO == null) {
            throw new IllegalArgumentException("Juego no encontrado con ID: " + gameId);
        }

        Game game = DTOMapper.toGameEntity(gameDTO);
        game.setEndDate(endDate);
        game.setGameQuestions(gameQuestions.stream()
                .map(DTOMapper::toGameQuestionEntity)
                .toList());

        game = this.calculateScore(game);
        gameDao.save(game);

        return game.getScore();
    }

    @Override
    public List<RankingDTO> findTopRanking(int numberOfGames) {
        return this.gameDao.findTopRanking(numberOfGames)
            .stream()
            .map(DTOMapper::toRankingDTO)
            .toList();
    }

    public Game calculateScore(Game game) {
        long score = 0;
        long timeTaken = 0;
        int totalQuestions = game.getGameQuestions().size();
        int numberCorrectQuestions = 0;
        String mode = game.getMode().name();

        if(mode.equals("EASY") || mode.equals("MEDIUM") || mode.equals("HARD")) {
            
            numberCorrectQuestions = game.getNumberOfQuestions();
            long difficultyFactor = this.calculateQuestionDifficultyFactor(mode);
            

            for(GameQuestion gq : game.getGameQuestions()) {
                timeTaken += (gq.getFinish().getTime() - gq.getStart().getTime()) / 1000;
            }
            
            long timeFactor = this.calculateTimeFactor(timeTaken);

            score =  numberCorrectQuestions / totalQuestions * difficultyFactor * timeFactor;
        } else {
            for (GameQuestion gq : game.getGameQuestions()) {
                numberCorrectQuestions = gq.isValid() ? 1 : 0;

                long difficultyFactor = this.calculateQuestionDifficultyFactor(gq.getQuestion().getLevel().name());

                long questionTimeTaken = (gq.getFinish().getTime() - gq.getStart().getTime()) / 1000;
                long timeFactor = this.calculateTimeFactor(questionTimeTaken);
                timeTaken += questionTimeTaken;

                score +=  numberCorrectQuestions / totalQuestions * difficultyFactor * timeFactor;
            }
        }

        game.setScore(score);
        game.setTime(timeTaken);

        return game;
    }

    public long calculateQuestionDifficultyFactor(String difficulty) {

        // Factor de dificultad: 1 punto para fácil, 3 puntos para medio, 5 puntos para difícil
        switch (difficulty) {
            case "EASY":
                return 1L;
            case "MEDIUM":
                return 3L;
            case "HARD":
                return 5L;
            default:
                throw new IllegalArgumentException("Dificultad no válida: " + difficulty);
        }
    }

    public long calculateTimeFactor(long timeTaken) {
        // Factor de tiempo: 5 puntos <= 5 segundos, 3 puntos 5 < x <= 20 segundos, 1 punto x > 20 segundos
        if (timeTaken <= 5) {
            return 5L;
        } else if (timeTaken <= 20) {
            return 3L;
        } else {
            return 1L;
        }
    }

    public long calculateFactors(String difficult, Date start, Date finish) {
        long difficultyFactor = this.calculateQuestionDifficultyFactor(difficult);

        long timeTaken = (finish.getTime() - start.getTime()) / 1000;
        long timeFactor = this.calculateTimeFactor(timeTaken);

        return difficultyFactor * timeFactor;
    }
}
