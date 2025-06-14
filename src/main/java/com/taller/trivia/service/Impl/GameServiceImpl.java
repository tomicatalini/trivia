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
import com.taller.trivia.model.Answer;
import com.taller.trivia.model.Game;
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
    public GameDTO createGame(Long quizId, Long userId, Long categoryId, String level, int numberOfQuestions) {
        if (quizId == null || userId == null) {
            throw new IllegalArgumentException("El ID del cuestionario y el ID del jugador no pueden ser nulos.");
        }

        if (numberOfQuestions <= 0) {
            throw new IllegalArgumentException("El número de preguntas debe ser mayor que 0.");
        }

        Quiz quiz = this.quizDao.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado con ID: " + quizId));
        User user = this.userDao.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        Game game = new Game();
        game.setQuiz(quiz);
        game.setUser(user);
        game.setStartDate(new Date());
        game.setEndDate(null);
        game.setScore(0L);
    
        List<Question> questions = this.questionDao.findRandomQuestions(categoryId, quizId, level, numberOfQuestions);

        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron preguntas para el juego con ID: " + game.getId());            
        } else {
            System.out.println("Se encontraron " + questions.size() + " preguntas para el juego con ID: " + game.getId());
        }

        List<GameQuestion> gameQuestions = new ArrayList<>();

        System.out.println("Preparando GameQuestionDTOs");
        for (Question question : questions) {
            
            for (Answer answer : question.getAnswers()) {
                System.out.println("Id de la respuesta: " + answer.getId());
                System.out.println("Texto de la respuesta: " + answer.getAnswer());
                System.out.println("Es correcta: " + (answer.isValid() ? "Sí" : "No"));
            }

            GameQuestion gameQuestion = new GameQuestion(
                game,
                question,
                null,
                null
            );

            gameQuestions.add(gameQuestion);
        }

        game.setGameQuestions(gameQuestions);

        System.out.println("Guardando GameQuestionDTOs");
        game = this.gameDao.save(game);        
        //this.saveGameQuestions(categoryId, gameQuestions);

        System.out.println("retorno el GameDTO");
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
    public void endGame(Long gameId, Date endDate, List<GameQuestionDTO> gamesQuestions) {
        Game game = DTOMapper.toGameEntity(this.getGameById(gameId));

        if (game == null) {
            throw new IllegalArgumentException("Juego no encontrado con ID: " + gameId);
        }

        game.setEndDate(endDate);

        this.saveGameQuestions(gameId, gamesQuestions);
        this.calculateScore(gameId);
    }

    @Override
    public void saveGameQuestions(Long gameId, List<GameQuestionDTO> gameQuestions) {
        if (gameId == null || gameQuestions == null || gameQuestions.isEmpty()) {
            throw new IllegalArgumentException("El ID del juego y la lista de preguntas no pueden ser nulos o vacíos.");
        }

        Game game = this.gameDao.findById(gameId).orElse(null);

        if (game == null) {
            throw new IllegalArgumentException("Juego no encontrado con ID: " + gameId);
        }

        game.setGameQuestions(gameQuestions.stream()
                .map(DTOMapper::toGameQuestionEntity)
                .toList());

        System.out.println("Guardando Game con ID: " + game.getId() + " y " + game.getGameQuestions().size() + " preguntas.");

        System.out.println(game.getScore());
        System.out.println(game.getStartDate());
        System.out.println(game.getEndDate());
        System.out.println(game.getUser().getName());
        System.out.println(game.getQuiz().getName());
        try {
            this.gameDao.save(game);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar el juego: " + e.getMessage());
        }
        
    }


    public Long calculateScore(Long gameId) {
        Game game = this.gameDao.findById(gameId).orElse(null);
        Long score = 0L;
        int totalQuestions = game.getGameQuestions().size();

        for (GameQuestion gameQuestion : game.getGameQuestions()) {    
            Long difficultyFactor = this.calculateQuestionDifficultyFactor(gameQuestion.getQuestion());
            Long timeFactor = this.calculateTimeFactor(gameQuestion.getStart(), gameQuestion.getFinish());
            score += this.calculateScore(totalQuestions, gameQuestion.isValid(), difficultyFactor, timeFactor);
        }
        score = score / totalQuestions;
        game.setScore(score);
        this.gameDao.save(game);
        return score;
    }

    public Long calculateQuestionDifficultyFactor(Question question) {
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

    public Long calculateTimeFactor(Date start, Date finish) {
        if (start == null || finish == null) {
            throw new IllegalArgumentException("La fecha de inicio y la fecha de finalización no pueden ser nulas.");
        }

        Long timeTaken = (finish.getTime() - start.getTime()) / 1000; // Tiempo en segundos

        // Factor de tiempo: 5 puntos <= 5 segundos, 3 puntos 5 < x <= 20 segundos, 1 punto x > 20 segundos
        if (timeTaken <= 5) {
            return 5L;
        } else if (timeTaken <= 20) {
            return 3L;
        } else {
            return 1L;
        }
    }

    public Long calculateScore(int totalQuestions, boolean correctAnswers, Long difficultyFactor, Long timeFactor) {
        int valid = 0;

        if (correctAnswers == true) {
            valid = 1;
        }

        return valid * difficultyFactor * timeFactor;
    }
}
