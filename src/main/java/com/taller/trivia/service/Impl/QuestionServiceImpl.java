package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dto.QuestionDTO;
import com.taller.trivia.dto.QuizDTO;
import com.taller.trivia.model.Question;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.service.QuestionService;

public class QuestionServiceImpl implements QuestionService {
    
    @Autowired
    private QuestionDao repository;

    @Override
    public List<UserDTO> getAll() {
        return repository.findAll().stream()
                         .map(this::userToDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getById(Long id) {
        return repository.findById(id)
                         .map(this::userToDTO);
    }

    @Override
    public UserDTO save(UserDTO userDto, String password) {
        
        if (userDto == null) {
            throw new IllegalArgumentException("El objeto userDto no puede ser nulo");
        }
        
        if (userDto.getName() == null || userDto.getEmail() == null || password.isBlank()) {
            throw new IllegalArgumentException("Los campos 'name', 'email' y 'password' son obligatorios");
        }

        User user = this.DTOToUser(userDto);
        String encodedPass = passwordEncoder.encode(password);

        user.setPassword(encodedPass);
        user = repository.save(user);
        return this.userToDTO(user);
    }

    @Override
    public UserDTO update(Long userId, UserDTO userDto) {
        User user = this.repository.update(userId, this.DTOToUser(userDto));        
        return this.userToDTO(user);
    };

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    //Metodos de soporte
    public QuestionDTO toDTO(Question question) {
        QuestionDTO questionDto = new QuestionDTO();
        
        questionDto.setQuestion(question.getQuestion());
        questionDto.setLevel(question.getLevel());
        questionDto.setQuiz(this.quizToDTO(question.getQuiz()));
        questionDto.setType(question.getType());
        questionDto.setAnswers(null);

        return questionDto;
    }

    public Question toEntity(QuestionDTO questionDto) {
        Question question = new Question();
    
        
        return question;
    }

    public QuizDTO quizToDTO(Quiz quiz) {
        QuizDTO quizDto = new QuizDTO();

        quizDto.setId(quiz.getId());
        quizDto.setName(quiz.getName());
        quizDto.setUrl(quiz.getUrl());

        return quizDto;
    }


}
