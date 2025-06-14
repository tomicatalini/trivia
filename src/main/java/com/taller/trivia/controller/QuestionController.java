package com.taller.trivia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.trivia.dto.LevelDTO;
import com.taller.trivia.dto.QuestionDTO;
import com.taller.trivia.service.QuestionService;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.util.ResponseHandler;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<?> getAllQuestions() {
        try {
            List<QuestionDTO> questions = questionService.getAll();
            return ResponseHandler.handleResponse(questions);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessageLoader.getMessage("SERVER_ERROR"), e.getMessage());
        }
    }

    @GetMapping("/level")
    public ResponseEntity<?> getQuestionsLevel(){
        try {
            List<LevelDTO> levels = questionService.getQuestionsLevel();
            return ResponseHandler.handleResponse(levels);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessageLoader.getMessage("SERVER_ERROR"), e.getMessage());
        }
    }
}
