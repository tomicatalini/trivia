package com.taller.trivia.service.Impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.trivia.dao.GameDao;
import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.ProgressDTO;
import com.taller.trivia.dto.QuestionDTO;
import com.taller.trivia.model.Game;
import com.taller.trivia.model.GameQuestionId;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.model.User;
import com.taller.trivia.service.GameService;
import com.taller.trivia.service.QuestionService;
import com.taller.trivia.service.QuizService;
import com.taller.trivia.service.UserService;
import com.taller.trivia.util.DTOMapper;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuizService quizService;
    @Autowired
    private UserService userService;

    @Autowired
    private GameDao gameDao;

    @Override
    public void createGame(Long quizId, Long userId, Long categoryId, int numberOfQuestions) {
        if (quizId == null || userId == null) {
            throw new IllegalArgumentException("El ID del cuestionario y el ID del jugador no pueden ser nulos.");
        }

        if (numberOfQuestions <= 0) {
            throw new IllegalArgumentException("El número de preguntas debe ser mayor que 0.");
        }

        Quiz quiz = DTOMapper.toQuizEntity(quizService.getById(quizId).orElse(null));
        User user = DTOMapper.toUserEntity(userService.getById(userId).orElse(null));

        Game game = new Game();
        game.setQuiz(quiz);
        game.setUser(user);
        game.setStartDate(new Date());
        game.setEndDate(null);
        game.setScore(0L);
        game.setGameQuestions(null);
        this.gameDao.save(game);
        
        List<GameQuestionDTO> questions = this.questionService.getRandomQuestions(quizId, categoryId, game.getId(), numberOfQuestions);
        game.setGameQuestions(questions.stream()
                .map(DTOMapper::toGameQuestionEntity)
                .toList());
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
    public void startGame(Long gameId, Long playerId) {
        // TODO: Implementar lógica de inicio de juego
    }

    @Override
    public void getAllGamesByPlayerId(Long playerId) {
        // TODO: Implementar lógica para obtener todos los juegos de un jugador por su ID
        // Ejemplo: Buscar en la base de datos o en un repositorio
        throw new UnsupportedOperationException("Método no implementado");
    } 

    @Override
    public void endGame(Long gameId) {
        // TODO: Implementar lógica para finalizar el juego
        this.getGameById(gameId);
    }

    @Override
    public void saveGameProgress(Long gameId, Long playerId, ProgressDTO progress) {
        // TODO: Implementar lógica para guardar el progreso del juego
    }

    public Long calculateScore(Long correctAnswers, Long totalQuestions, Long difficultyFactor, Long timeFactor) {
        if (totalQuestions == 0) {
            throw new IllegalArgumentException("El total de preguntas no puede ser 0.");
        }
        // Calculo de puntaje
        // Puntaje = (Respuesta correcta / Total de preguntas) * factor de dificultad * factor tiempo
        // Factor de dificultad: 1 punto para fácil, 3 puntos para medio, 5 puntos para difícil
        // Factor de tiempo: 5 puntos <= 5 segundos, 3 puntos 5 < x <= 20 segundos, 1 punto x > 20 segundos
        return (correctAnswers * difficultyFactor * timeFactor) / totalQuestions;
    }

    public Long calculateQuestionDifficultyFactor(GameQuestionId gameQuestionId) {
        QuestionDTO question = this.questionService.getById(gameQuestionId.getQuestionId()).orElse(null);

        if (question == null) {
            throw new IllegalArgumentException("Pregunta no válida: " + gameQuestionId.getQuestionId());
        }

        String difficulty = question.getLevel().toString().toLowerCase();
        // Factor de dificultad: 1 punto para fácil, 3 puntos para medio, 5 puntos para difícil
        switch (difficulty) {
            case "easy":
                return 1L;
            case "medium":
                return 3L;
            case "hard":
                return 5L;
            default:
                throw new IllegalArgumentException("Dificultad no válida: " + difficulty);
        }
    }
}
