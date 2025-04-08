package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.dao.QuizDao;
import com.taller.trivia.dto.QuizDTO;
import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.ServiceException;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.service.QuizService;
import com.taller.trivia.util.DTOMapper;
import com.taller.trivia.util.ErrorMessageLoader;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizDao repository;

    @Override
    public List<QuizDTO> getAll() {
        try {
            return repository.findAll().stream()
                             .map(DTOMapper::toQuizDTO)
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public Optional<QuizDTO> getById(Long id) {
        try {
            return repository.findById(id)
                             .map(DTOMapper::toQuizDTO);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Transactional
    @Override
    public QuizDTO save(QuizDTO quizDto) {
        try {
            if (quizDto == null || quizDto.getName() == null || quizDto.getUrl() == null) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "nombre, url"));
            }
            Quiz quiz = DTOMapper.toQuizEntity(quizDto);
            quiz = repository.save(quiz);
            return DTOMapper.toQuizDTO(quiz);
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

}