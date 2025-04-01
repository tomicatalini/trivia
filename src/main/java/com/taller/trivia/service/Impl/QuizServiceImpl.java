package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.dao.QuizDao;
import com.taller.trivia.dto.QuizDTO;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.service.QuizService;

public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizDao repository;

    @Override
    public List<QuizDTO> getAll() {
        return repository.findAll().stream()
                         .map(this::quizToDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<QuizDTO> getById(Long id) {
        return repository.findById(id)
                         .map(this::quizToDTO);
    }

    @Override
    public QuizDTO save(QuizDTO quizDto) {
        
        if (quizDto == null) {
            throw new IllegalArgumentException("El objeto quizDto no puede ser nulo");
        }
        
        if (quizDto.getName() == null || quizDto.getUrl() == null) {
            throw new IllegalArgumentException("Los campos 'name', 'email' y 'password' son obligatorios");
        }

        Quiz quiz = this.DTOToQuiz(quizDto);

        quiz = repository.save(quiz);
        return this.quizToDTO(quiz);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }


    //Metodos de soporte
    public QuizDTO quizToDTO(Quiz quiz) {
        QuizDTO quizDTO = new QuizDTO();
        
        quizDTO.setName(quiz.getName());
        quizDTO.setUrl(quiz.getUrl());
        
        return quizDTO;
    }

    public Quiz DTOToQuiz(QuizDTO quizDTO) {
        Quiz user = new Quiz();
        
        user.setName(quizDTO.getName());
        user.setUrl(quizDTO.getUrl());
        
        return user;
    }
}