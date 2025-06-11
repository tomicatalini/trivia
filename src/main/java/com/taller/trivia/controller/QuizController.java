package com.taller.trivia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.trivia.dto.QuizDTO;
import com.taller.trivia.service.QuizService;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.util.ResponseHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Endpoint obtener todos los conjuntos de preguntas.
     * 
     * @return ResponseEntity containing a list of QuizDTOs or an error message.
     */
    @GetMapping
    public ResponseEntity<?> getAllQuizzes() {
        try {
            List<QuizDTO> quizzes = quizService.getAll();
            return ResponseHandler.handleResponse(quizzes);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessageLoader.getMessage("SERVER_ERROR"), e.getMessage());
        }
    }
}
