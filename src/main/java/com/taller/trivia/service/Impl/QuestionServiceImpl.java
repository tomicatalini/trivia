package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.service.GameService;
import com.taller.trivia.service.QuestionService;
import com.taller.trivia.util.DTOMapper;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dto.GameDTO;
import com.taller.trivia.dto.GameQuestionDTO;
import com.taller.trivia.dto.QuestionDTO;
import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.ServiceException;
import com.taller.trivia.model.Question;

@Service
public class QuestionServiceImpl implements QuestionService{

    @Autowired
    private QuestionDao repository;

    @Autowired
    private GameService gameService;
    
    @Transactional
    @Override
    public QuestionDTO save(QuestionDTO questionDto) {
        try {
            if (questionDto == null || questionDto.getQuestion() == null || questionDto.getType() == null || questionDto.getLevel() == null
            || questionDto.getCategory() == null) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "question, type, level, category"));
            }
            Question question = DTOMapper.toQuestionEntity(questionDto);
            question = repository.save(question);
            return DTOMapper.toQuestionDTO(question);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        try {
            if (!repository.delete(id)) {
                throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public Optional<QuestionDTO> getById(Long id) {
        try {
            return repository.findById(id)
                             .map(DTOMapper::toQuestionDTO);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public List<QuestionDTO> getAll() {
        try {
            return repository.findAll().stream()
                             .map(DTOMapper::toQuestionDTO)
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }


    @Override
    public List<QuestionDTO> getByCategory(Long id) {
        try {
            return repository.findAllByCategory(id).stream()
                             .map(DTOMapper::toQuestionDTO)
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
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