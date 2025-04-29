
package com.taller.trivia.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.taller.trivia.dao.AnswerDao;
import com.taller.trivia.dao.CategoryDao;
import com.taller.trivia.dao.QuestionDao;
import com.taller.trivia.dto.OpenTriviaCategoryDTO;
import com.taller.trivia.dto.OpenTriviaCategoryResponseDTO;
import com.taller.trivia.dto.OpenTriviaQuestionDTO;
import com.taller.trivia.dto.OpenTriviaResponseDTO;
import com.taller.trivia.dto.OpenTriviaTokenResponseDTO;
import com.taller.trivia.model.Answer;
import com.taller.trivia.model.Category;
import com.taller.trivia.service.TriviaImportService;

@Service
public class TriviaImportServiceImpl implements TriviaImportService {

    private final WebClient webClient;
    private final QuestionDao questionDao;
    private final CategoryDao categoryDao;
    private final AnswerDao answerDao;
    private String token;

    @Autowired
    public TriviaImportServiceImpl(QuestionDao questionDao,
                                   CategoryDao categoryDao,
                                   AnswerDao answerDao,
                                   WebClient.Builder webClientBuilder) {
        this.questionDao = questionDao;
        this.categoryDao = categoryDao;
        this.answerDao = answerDao;
        this.webClient = webClientBuilder.baseUrl("https://opentdb.com").build();
        this.token = fetchToken();
    }

    @Override
    public void importTriviaData() {
        OpenTriviaCategoryResponseDTO categoryResponse = webClient.get()
            .uri("/api_category.php")
            .retrieve()
            .bodyToMono(OpenTriviaCategoryResponseDTO.class)
            .block();

        if (categoryResponse != null && categoryResponse.getTrivia_categories() != null) {
            for (OpenTriviaCategoryDTO categoryDTO : categoryResponse.getTrivia_categories()) {
                importQuestionsForCategory(categoryDTO);
            }
        } else {
            System.out.println("No se pudieron obtener categorías desde la API.");
        }
    }

    private void importQuestionsForCategory(int categoryId) {
        int amount = 50;
        String url = String.format("/api.php?amount=%d&category=%d&type=multiple&token=%s",
                amount, categoryId, token);

        OpenTriviaResponseDTO response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OpenTriviaResponseDTO.class)
                .block();

        if (response != null) {
            if (response.getResponse_code() == 4) {
                this.token = resetToken();
                importQuestionsForCategory(categoryId);
                return;
            } else if (response.getResponse_code() == 0) {
                Category category = categoryDao.findByName(categoryDTO.getName());
                if (category == null) {
                    category = new Category();
                    category.setName(categoryDTO.getName());
                    categoryDao.save(category);
                }

                for (OpenTriviaQuestionDTO dto : response.getResults()) {
                    if (!questionDao.existsByText(dto.getQuestion())) {
                        Question question = new Question();
                        question.setText(dto.getQuestion());
                        question.setCategory(category);
                        question.setDifficulty(dto.getDifficulty());
                        questionDao.save(question);

                        Answer correct = new Answer(dto.getCorrect_answer(), true, question);
                        answerDao.save(correct);

                        for (String wrong : dto.getIncorrect_answers()) {
                            Answer incorrect = new Answer(wrong, false, question);
                            answerDao.save(incorrect);
                        }
                    }
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

