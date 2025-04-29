
package com.taller.trivia.service.Impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.taller.trivia.dao.AnswerDao;
import com.taller.trivia.dao.CategoryDao;
import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dao.QuizDao;
import com.taller.trivia.dto.OpenTriviaCategoryDTO;
import com.taller.trivia.dto.OpenTriviaCategoryResponseDTO;
import com.taller.trivia.dto.OpenTriviaQuestionDTO;
import com.taller.trivia.dto.OpenTriviaResponseDTO;
import com.taller.trivia.dto.OpenTriviaTokenResponseDTO;
import com.taller.trivia.model.Answer;
import com.taller.trivia.model.Category;
import com.taller.trivia.model.Level;
import com.taller.trivia.model.Question;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.service.TriviaImportService;
import com.taller.trivia.util.DTOMapper;

@Service
public class TriviaImportServiceImpl implements TriviaImportService {

    private final WebClient webClient;
    private final QuestionDao questionDao;
    private final CategoryDao categoryDao;
    private final QuizDao quizDao;
    private final AnswerDao answerDao;
    private String token;

    @Autowired
    public TriviaImportServiceImpl(QuestionDao questionDao,
                                   CategoryDao categoryDao,
                                   AnswerDao answerDao,
                                   QuizDao quizDao,
                                   WebClient.Builder webClientBuilder) {
        this.questionDao = questionDao;
        this.categoryDao = categoryDao;
        this.answerDao = answerDao;
        this.quizDao = quizDao;
        this.webClient = webClientBuilder.baseUrl("https://opentdb.com").build();
        this.token = fetchToken();
    }

    @Override
    public void importTriviaData(Long quizId) {
        OpenTriviaCategoryResponseDTO categoryResponse = webClient.get()
            .uri("/api_category.php")
            .retrieve()
            .bodyToMono(OpenTriviaCategoryResponseDTO.class)
            .block();

        if (categoryResponse != null && categoryResponse.getTrivia_categories() != null) {
            for (OpenTriviaCategoryDTO categoryDTO : categoryResponse.getTrivia_categories()) {
                importQuestionsForCategory(quizId, categoryDTO);
            }
        } else {
            System.out.println("No se pudieron obtener categorías desde la API.");
        }
    }

    private void importQuestionsForCategory(Long quizId, OpenTriviaCategoryDTO categoryDTO) {
        int limitAmount = 50;
        int currentAmount = -1;
        Quiz quiz = quizDao.findById(quizId).orElse(null);

        while (currentAmount == -1 || currentAmount > 0) {
            String url = String.format("/api.php?amount=%d&category=%d&type=multiple&token=%s",
                limitAmount, categoryDTO.getId(), token);

            OpenTriviaResponseDTO response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenTriviaResponseDTO.class)
                    .delayElement(Duration.ofSeconds(6l))
                    .block();        

            if (response != null) {
                currentAmount = response.getResults().size();

                if (response.getResponse_code() == 4) {
                    this.token = resetToken();
                    importQuestionsForCategory(quizId, categoryDTO);
                    return;
                } else if (response.getResponse_code() == 0) {
                    Category category = categoryDao.findById((long) categoryDTO.getId()).orElse(null);
                        
                    if (category == null) {
                        Category newCategory = new Category(categoryDTO.getName(),categoryDTO.getName(),true);
                        categoryDao.save(newCategory);
                        category = newCategory;
                    }

                    for (OpenTriviaQuestionDTO dto : response.getResults()) {
                        System.out.println("Importando pregunta: " + dto.getQuestion() + " de la categoría: " + category.getTitle() + " con dificultad: " + dto.getDifficulty() + " y tipo: " + dto.getType());
                        Question question = new Question();
                        question.setQuestion(dto.getQuestion());
                        question.setCategory(category);
                        question.setLevel(Level.valueOf(dto.getDifficulty().toUpperCase()));
                        question.setType(dto.getType());
                        question.setQuiz(quiz);

                        List<Answer> answers = new ArrayList<>();

                        Answer correct = new Answer(dto.getCorrect_answer(), true, question);
                        //answerDao.save(correct);
                        answers.add(correct);

                        for (String wrong : dto.getIncorrect_answers()) {
                            Answer incorrect = new Answer(wrong, false, question);
                            //answerDao.save(incorrect);
                            answers.add(incorrect);
                        }
                        
                        question.setAnswers(answers);
                        //questionDao.save(question);
                    }
                }
            } else {
                currentAmount = 0;
            }
        }
    }

    private String fetchToken() {
        OpenTriviaTokenResponseDTO tokenResponse = webClient.get()
            .uri("/api_token.php?command=request")
            .retrieve()
            .bodyToMono(OpenTriviaTokenResponseDTO.class)
            .block();

        return tokenResponse != null ? tokenResponse.getToken() : null;
    }

    private String resetToken() {
        OpenTriviaTokenResponseDTO tokenResponse = webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api_token.php")
                    .queryParam("command", "reset")
                    .queryParam("token", token)
                    .build())
            .retrieve()
            .bodyToMono(OpenTriviaTokenResponseDTO.class)
            .block();

        return tokenResponse != null ? tokenResponse.getToken() : fetchToken();
}

}

