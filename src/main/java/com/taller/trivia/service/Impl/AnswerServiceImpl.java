package com.taller.trivia.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.dao.AnswerDao;
import com.taller.trivia.dto.AnswerDTO;
import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.ServiceException;
import com.taller.trivia.model.Answer;
import com.taller.trivia.model.User;
import com.taller.trivia.service.AnswerService;
import com.taller.trivia.util.ErrorMessageLoader;

public class AnswerServiceImpl { 
    // implements AnswerService {

    // @Autowired
    // private AnswerDao repository;

    // @Override
    // public AnswerDTO save(AnswerDTO answerDTO) {
    //     try {
    //         if (userDto == null || userDto.getName() == null || userDto.getEmail() == null) {
    //             throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "nombre, email"));
    //         }
    //         User user = this.DTOToUser(userDto);
    //         String encodedPass = passwordEncoder.encode(password);
    //         user.setPassword(encodedPass);
    //         user = repository.save(user);
    //         return this.userToDTO(user);
    //     } catch (BusinessException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
    //     }
    // };
    
    // @Override
    // public boolean delete(Long id);
    
    // @Override
    // public Optional<Answer> findById(Long id);
}
