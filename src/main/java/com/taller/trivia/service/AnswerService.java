package com.taller.trivia.service;

import java.util.Optional;

import com.taller.trivia.dto.AnswerDTO;

public interface AnswerService {
    AnswerDTO save(AnswerDTO answerDTO);
    boolean delete(Long answerId);
    Optional<AnswerDTO> findById(Long answerId);
}
