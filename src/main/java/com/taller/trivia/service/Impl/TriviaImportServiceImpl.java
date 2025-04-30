package com.taller.trivia.service.Impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@Transactional
public class TriviaImportServiceImpl implements TriviaImportService {

    private final WebClient webClient;
    private final QuestionDao questionDao;
    private final CategoryDao categoryDao;
    private final QuizDao quizDao;
    private String token;

    public TriviaImportServiceImpl(QuestionDao questionDao,
                                   CategoryDao categoryDao,
                                   AnswerDao answerDao,
                                   QuizDao quizDao,
                                   WebClient.Builder webClientBuilder) {
        this.questionDao = questionDao;
        this.categoryDao = categoryDao;
        this.quizDao = quizDao;
        this.webClient = webClientBuilder.baseUrl("https://opentdb.com").build();
        this.token = fetchToken();
        this.token = resetToken();
    }

    @Override    
    public void importTriviaData(Long quizId) {
        Quiz quiz = quizDao.findById(quizId).orElse(null);
        
        if(quiz == null) {
            System.out.println("No se encontró el quiz con ID: " + quiz.getId());
            throw new RuntimeException("Quiz not found");
        }
    
        OpenTriviaCategoryResponseDTO categoryResponse = webClient.get()
            .uri("/api_category.php")
            .retrieve()
            .bodyToMono(OpenTriviaCategoryResponseDTO.class)
            .block();

        if (categoryResponse != null && categoryResponse.getTrivia_categories() != null) {
            for (OpenTriviaCategoryDTO categoryDTO : categoryResponse.getTrivia_categories()) {
                //OpenTriviaCategoryDTO categoryDTO = new OpenTriviaCategoryDTO(25, "Art");

                System.out.println("Obtengo CATEGORIA con ID: " + categoryDTO.getId() + " y nombre: " + categoryDTO.getName());
                Category category = categoryDao.findByTitle(categoryDTO.getName()).orElse(null);
                        
                if (category == null) {
                    category = createCategory(categoryDTO.getId(), categoryDTO.getName());
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
        int totalQuestionLoaded = 0;
        int responseCode = 0;

        while (responseCode == 0) {

            if(limitAmount > amount) {
                limitAmount = amount;
            }

            if (amount - totalQuestionLoaded < limitAmount) {
                limitAmount = amount - totalQuestionLoaded;            
            }

            if(limitAmount == 0){
                return;
            }

            //String url = String.format("/api.php?amount=%d&category=%d&type=multiple&token=%s", limitAmount, category.getId(), token);
            String url = String.format("/api.php?amount=%d&category=%d&token=%s", limitAmount, category.getId(), token);
            OpenTriviaResponseDTO response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenTriviaResponseDTO.class)
                    .delayElement(Duration.ofSeconds(5L))
                    .block();
                    
            if (response == null) {
                System.out.println("No se pudo obtener la respuesta de la API.");
                throw new RuntimeException("No se pudo obtener la respuesta de la API.");
            }

            responseCode = response.getResponse_code();

            switch (responseCode) {
                case 1:
                    System.out.println("No results: Could not return results. The API does not have enough questions for the specified parameters.");
                    break;
                case 2:
                    System.out.println("Invalid parameter: The API does not have enough questions for the specified parameters.");
                    break;
                case 3:
                    System.out.println("Token not found: The token provided is invalid or expired.");
                    break;
                case 4:
                    System.out.println("Token empty: Session token has returned all possible questions for the specidied query");
                    break;
                case 5:
                    System.out.println("Rate limit: too many requests in a short period of time. Please wait a while before trying again.");
                    break;
                default:
                        int count = 0;
                        for (OpenTriviaQuestionDTO dto : response.getResults()) {
                            count++;
                            
                            Question question = new Question();
                            question.setQuestion(dto.getQuestion());
                            question.setCategory(category);
                            question.setLevel(Level.valueOf(dto.getDifficulty().toUpperCase()));
                            question.setType(dto.getType());
                            question.setQuiz(quiz);

                            List<Answer> answers = new ArrayList<>();

                            Answer correct = new Answer(dto.getCorrect_answer(), true, question);
                            answers.add(correct);

                            for (String wrong : dto.getIncorrect_answers()) {
                                Answer incorrect = new Answer(wrong, false, question);
                                answers.add(incorrect);
                            }
                            
                            question.setAnswers(answers);
                            questionDao.save(question); // Guarda la pregunta
                        }

                        totalQuestionLoaded += response.getResults().size();        
                        System.out.println("Se importaron " + count + " preguntas de la categoría: " + category.getTitle() + " con ID: " + category.getId());
                    break;
            }
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

    private Category createCategory(int id, String name) {
        Category category = new Category();
        category.setId((long) id);
        category.setTitle(name);
        category.setDescription(name);
        category.setEnable(true);

        categoryDao.save(category);

        return category;
    }
}

