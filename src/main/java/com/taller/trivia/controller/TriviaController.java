package com.taller.trivia.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.taller.trivia.service.TriviaImportService;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.util.ResponseHandler;

@RestController
@RequestMapping("/trivia")
public class TriviaController {
    private final TriviaImportService triviaService;

    public TriviaController(TriviaImportService triviaService) {
        this.triviaService = triviaService;
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<?> importTriviaData() {
        try {
            System.out.println("Comienza el proceso de importación de datos de trivia");
            System.out.println("Inicio:" + new Date());
            System.out.println();
            this.triviaService.importTriviaData(1L);

            System.out.println("Fin:" + new Date());

            return ResponseHandler.handleResponse(null);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        ErrorMessageLoader.getMessage("SERVER_ERROR"),
                                                        e.getMessage());
        }
    }
}
