package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.service.GameService;
import com.taller.trivia.service.QuestionService;
import com.taller.trivia.util.DTOMapper;
import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.QuestionDTO;
import com.taller.trivia.model.Question;

public class QuestionServiceImpl implements QuestionService{

    @Autowired
    private QuestionDao repository;

    @Autowired
    private GameService gameService;
    
    @Override
    public QuestionDTO save(QuestionDTO questionDto) {
        
        if (questionDto == null) {
            throw new IllegalArgumentException("El objeto questionDto no puede ser nulo");
        }
        
        if (questionDto.getQuestion() == null || questionDto.getType() == null || questionDto.getLevel() == null
            || questionDto.getCategory() == null) {
            throw new IllegalArgumentException("Los campos 'question', 'type', 'level' y 'category' son obligatorios");
        }

        Question question = DTOMapper.toQuestionEntity(questionDto);

        question = repository.save(question);
        return DTOMapper.toQuestionDTO(question);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    } 

    @Override
    public Optional<QuestionDTO> getById(Long id) {
        return repository.findById(id)
                         .map(DTOMapper::toQuestionDTO);
    }

    @Override
    public List<QuestionDTO> getAll() {
        return repository.findAll().stream()
                         .map(DTOMapper::toQuestionDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDTO> getByCategory(Long id) {
        return repository.findAllByCategory(id).stream()
                         .map(DTOMapper::toQuestionDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDTO> getQuestionsGame(Long gameId) {
        return repository.findAllQuestionsGame(gameId).stream()
                         .map(DTOMapper::toQuestionDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public List<GameQuestionDTO> getRandomQuestions(Long quizId, Long categoryId, Long gameId, String level, int numberOfQuestions) {
        GameDTO gameDTO = gameService.getGameById(gameId);        
        List<Question> questions = repository.findRandomQuestions(categoryId, quizId, level, numberOfQuestions);
        
        return questions.stream()
                .map((question) -> {
                    GameQuestionDTO gameQuestionDTO = new GameQuestionDTO();
                    gameQuestionDTO.setQuestion(DTOMapper.toQuestionDTO(question));
                    gameQuestionDTO.setGame(gameDTO);
                    return gameQuestionDTO;
                })
                .collect(Collectors.toList());
    }
}