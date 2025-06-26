package com.taller.trivia.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.taller.trivia.dto.AnswerDTO;
import com.taller.trivia.dto.AuthDTO;
import com.taller.trivia.dto.CategoryDTO;
import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.LevelDTO;
import com.taller.trivia.dto.QuestionDTO;
import com.taller.trivia.dto.QuizDTO;
import com.taller.trivia.dto.RankingDTO;
import com.taller.trivia.dto.UserDTO;
import com.taller.trivia.model.Answer;
import com.taller.trivia.model.Category;
import com.taller.trivia.model.Question;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.model.Rol;
import com.taller.trivia.model.User;
import com.taller.trivia.model.Game;
import com.taller.trivia.model.GameMode;
import com.taller.trivia.model.GameQuestion;
import com.taller.trivia.model.Level;


public class DTOMapper {
    
    //User <-> UserDTO
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
            user.getId(), 
            user.getName(), 
            user.getEmail(), 
            user.getRol());
    }

    public static User toUserEntity(UserDTO dto) {
        return new User(
            dto.getId(), 
            dto.getName(), 
            dto.getEmail(), 
            dto.getRol(),
            dto.getPassword()    
        );
    }

    public static User toUserEntity(long userId) {
        return new User(
            userId, 
            null, 
            null, 
            null,
            null    
        );
    }

    //Question <-> QuestionDTO
    public static QuestionDTO toQuestionDTO(Question question) {

        List<Answer> answers = question.getAnswers();
        Collections.shuffle(answers);

        return new QuestionDTO(
            question.getId(),
            question.getQuestion(),
            question.getType(),
            question.getLevel(),
            toCategoryDTO(question.getCategory()),            
            answers.stream()
                .map(DTOMapper::toAnswerDTO)
                .collect(Collectors.toList())
        );
    }

    public static Question toQuestionEntity(QuestionDTO dto) {
        return new Question(
                dto.getId(),
                dto.getQuestion(),
                dto.getType(), 
                dto.getLevel(),
                toCategoryEntity(dto.getCategory()),
                dto.getAnswers().stream()
                        .map(DTOMapper::toAnswerEntity)
                        .collect(Collectors.toList())
        );
    }

    //Answer <-> AnswerDTO
    public static AnswerDTO toAnswerDTO(Answer answer) {
        return new AnswerDTO(
            answer.getId(), 
            answer.getAnswer(), 
            answer.isValid()
        );
    }

    public static Answer toAnswerEntity(AnswerDTO dto) {
        return new Answer(
            dto.getId(), 
            dto.getAnswer(), 
            dto.isValid()
        );
    }

    //Quiz <-> QuizDTO
    public static QuizDTO toQuizDTO(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getName(),
                quiz.getUrl(),
                null
        );
    }

    public static Quiz toQuizEntity(QuizDTO dto) {
        return new Quiz(
                dto.getId(),
                dto.getName(),
                dto.getUrl(),
                null,
                null
        );
    }

    public static Quiz toQuizEntity(long quizId) {
        return new Quiz(
                quizId,
                null,
                null,
                null,
                null
        );
    }

    //Category <-> CategoryDTO
    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(
            category.getId(), 
            category.getTitle(), 
            category.getDescription(), 
            category.isEnable(),
            category.getQuizId()
        );
    }

    public static Category toCategoryEntity(CategoryDTO dto) {
        return new Category(
            dto.getId(), 
            dto.getCategory(), 
            dto.getDescription(), 
            dto.isEnable(),
            dto.getQuizId()
        );
    }

    //Game <-> GameDTO
    @SuppressWarnings("null")
    public static GameDTO toGameDTO(Game game) {

        return new GameDTO(
                game.getId(),
                game.getStartDate(),
                game.getEndDate(),
                game.getMode().name(),
                game.getNumberOfQuestions(),
                game.getScore(),
                game.getTime(),
                game.getUser() != null ? game.getUser().getId() : null,
                game.getQuiz() != null ? game.getQuiz().getId() : null,
                game.getGameQuestions().stream()
                        .map(DTOMapper::toGameQuestionDTO)
                        .collect(Collectors.toList())
        );
    }

    public static Game toGameEntity(GameDTO dto) {

        return new Game(
                dto.getGameId(),
                dto.getStartDate(),
                dto.getEndDate(),
                GameMode.valueOf(dto.getMode().toUpperCase()),
                dto.getNumberOfQuestions(),
                dto.getScore(),
                dto.getTime(),
                toUserEntity(dto.getUserId()),
                toQuizEntity(dto.getQuizId()),
                dto.getGameQuestions().stream()
                        .map(DTOMapper::toGameQuestionEntity)
                        .collect(Collectors.toList())
        );
    }

    public static Game toGameEntity(long gameId) {
        Game game = new Game();
        game.setId(gameId);

        return game;
    }

    //GameQuestion <-> GameQuestionDTO
    public static GameQuestion toGameQuestionEntity(GameQuestionDTO gameQuestionDTO) {
        return new GameQuestion(
            toGameEntity(gameQuestionDTO.getGameId()),
            toQuestionEntity(gameQuestionDTO.getQuestion()),
            gameQuestionDTO.getStart(),
            gameQuestionDTO.getFinish(),
            gameQuestionDTO.isValid()
        );
    }

    public static GameQuestionDTO toGameQuestionDTO(GameQuestion gameQuestion) {
        return new GameQuestionDTO(
            gameQuestion.getGame().getId(),
            toQuestionDTO(gameQuestion.getQuestion()),
            gameQuestion.getStart(),
            gameQuestion.getFinish(),
            gameQuestion.isValid()
        );
    }

    //Level <-> LevelDTO
    public static Level toLevelEntity(LevelDTO levelDTO) {
        return Level.valueOf(levelDTO.getName().toUpperCase());
    }

    public static LevelDTO toLevelDTO(Level level) {
        return new LevelDTO(level.name());
    }

    //AuthDTO
    public static AuthDTO toAuthDTO(User user) {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUserId(user.getId());
        authDTO.setUsername(user.getName());
        authDTO.setEmail(user.getEmail());
        authDTO.setAdmin(user.getRol().equals(Rol.ADMIN) ? true : false);
        return authDTO;
    }

    //RankingDTO
    public static RankingDTO toRankingDTO(Game game) {
        return new RankingDTO(
            toUserDTO(game.getUser()),
            game.getStartDate(),
            game.getScore(),
            game.getTime()
        );
    } 
}
