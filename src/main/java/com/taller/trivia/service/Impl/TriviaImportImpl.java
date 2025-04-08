
package com.taller.trivia.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.taller.trivia.dao.AnswerDao;
import com.taller.trivia.dao.CategoryDao;
import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dao.QuizDao;
import com.taller.trivia.dto.OpenTriviaQuestionDTO;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.service.TriviaImport;

@Service
public class TriviaImportImpl implements TriviaImport {
    // @Autowired
    // private final QuizDao quizDao;
    // @Autowired
    // private final QuestionDao questionDao;
    // @Autowired
    // private final AnswerDao answerDao;
    // @Autowired
    // private final CategoryDao categoryDao;

    @Override
    public void importTriviaData(Long quizId) {

        // // 1. Buscás el quiz por ID
        // Quiz quiz = quizDao.findById(quizId).orElse(null);

        // if (quiz == null) {
        //     throw new RuntimeException("Quiz not found");
        // }


        // WebClient.create().get()
        //     .uri(uriBuilder -> uriBuilder.path(quiz.getUrl())
        //         .queryParam("amount", amount)
        //         .queryParam("type", "multiple")
        //         .build())
        //     .retrieve()
        //     .bodyToMono(OpenTriviaQuestionDTO.class)
        //     .block();

        // // 2. Convertís y guardás
        // for (OpenTDBQuestionDTO dto : response.getResults()) {
        //     // acá mapeás de DTO a entidad
        //     // y llamás a tus DAOs para persistir cada una
        // }
    }


}
