
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
import com.taller.trivia.dto.OpenTriviaCategoryCountResponseDTO;
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
        System.out.println("Importando datos de trivia...");
        System.out.println("token: " + token);
        
        System.out.println("Obtenemos QUIZ...");
        Quiz quiz = quizDao.findById(quizId).orElse(null);
        
        if(quiz == null) {
            System.out.println("No se encontró el quiz con ID: " + quiz.getId());
            throw new RuntimeException("Quiz not found");
        }
        
        System.out.println("Obtenemos CATEGORÍAS...");
        OpenTriviaCategoryResponseDTO categoryResponse = webClient.get()
            .uri("/api_category.php")
            .retrieve()
            .bodyToMono(OpenTriviaCategoryResponseDTO.class)
            .block();

        System.out.println("Cantidad de categorías obtenidas: " + categoryResponse.getTrivia_categories().size());

        if (categoryResponse != null && categoryResponse.getTrivia_categories() != null) {
            for (OpenTriviaCategoryDTO categoryDTO : categoryResponse.getTrivia_categories()) {

                System.out.println("Obtengo CATEGORIA con ID: " + categoryDTO.getId() + " y nombre: " + categoryDTO.getName());
                Category category = categoryDao.findByTitle(categoryDTO.getName()).orElse(null);
                        
                if (category == null) {
                    Category newCategory = new Category((long) categoryDTO.getId(),categoryDTO.getName(),categoryDTO.getName(),true);
                    categoryDao.save(newCategory);
                    category = newCategory;
                }

                System.out.println("Obtengo cantidad de preguntas en la categoría: " + categoryDTO.getName() + " con ID: " + categoryDTO.getId());
                
                int totalQuestions = getQuestionCount(categoryDTO.getId());
                System.out.println("Cantidad de preguntas en la categoría: " + totalQuestions);

                importQuestionsForCategory(totalQuestions, category, quiz);
            }
        } else {
            System.out.println("No se pudieron obtener categorías desde la API.");
        }
    }

    private void importQuestionsForCategory(int amount, Category category, Quiz quiz) {
        int limitAmount = 50;

        while (amount > 0) {
            String url = String.format("/api.php?amount=%d&category=%d&type=multiple&token=%s",
                limitAmount, category.getId(), token);

            System.out.println("URL: " + url);

            OpenTriviaResponseDTO response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenTriviaResponseDTO.class)
                    .delayElement(Duration.ofSeconds(6L))
                    .block();        

            System.out.println("Response: " + response.getResponse_code());

            if (response != null) {
                
                if (response.getResponse_code() == 4) {
                    this.token = resetToken();
                    importQuestionsForCategory(amount, category, quiz);
                    return;
                } else if (response.getResponse_code() == 0) {

                    for (OpenTriviaQuestionDTO dto : response.getResults()) {
                        //System.out.println("Importando pregunta: " + dto.getQuestion() + " de la categoría: " + category.getTitle() + " con dificultad: " + dto.getDifficulty() + " y tipo: " + dto.getType());
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
                        questionDao.save(question);
                    }
                }
            }

            amount -= limitAmount;
            System.out.println("Preguntas restantes: " + amount);
        }
    }

    private int getQuestionCount(int categoryId) {
        String url = "https://opentdb.com/api_count.php?category=" + categoryId;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OpenTriviaCategoryCountResponseDTO.class)
                .block()
                .getCategory_question_count()
                .getTotal_question_count();
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

