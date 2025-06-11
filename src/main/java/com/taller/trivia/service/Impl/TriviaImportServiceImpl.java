package com.taller.trivia.service.Impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Quiz> quiz = quizDao.findById(quizId);
        
        if(quiz.isEmpty()) {
            System.out.println("No se encontró el quiz con ID: " + quizId);
            throw new RuntimeException("Quiz not found");
        }
        
        List<Category> categories = importCategories(quiz.get().getId());

        for (Category category : categories) {

            // if (category.getId() > 10) {
            //     return;
            // }
            
            // Obtengo la cantidad de preguntas existentes para la categoría
            int categoryQuestionCount = getCategoryQuestionCount(category.getId());

            // Llama al método público transaccional
            importQuestionsForCategory(categoryQuestionCount, category, quiz.get());
        }

    }

    @Transactional
    /**
     * Importa las categorías desde la API de Open Trivia y las guarda en la base de datos.
     * Si una categoría ya existe, la reutiliza.
     *
     * @return Lista de categorías importadas.
     */
    public List<Category> importCategories(Long quizId) {
        List<Category> categories = new ArrayList<>();

        // Obtiene las categorías desde la API de Open Trivia
        OpenTriviaCategoryResponseDTO categoryResponse = webClient.get()
            .uri("/api_category.php")
            .retrieve()
            .bodyToMono(OpenTriviaCategoryResponseDTO.class)
            .block();

        // Verifica si la respuesta contiene categorías
        if (categoryResponse != null && categoryResponse.getTrivia_categories() != null) {
            for (OpenTriviaCategoryDTO categoryDTO : categoryResponse.getTrivia_categories()) {
                Optional<Category> PersistedCategory = categoryDao.findById(Long.valueOf(categoryDTO.getId()));
                Category category = null;

                if (PersistedCategory.isEmpty()) {
                    System.out.println("CREATE: Nueva categoría con ID: " + categoryDTO.getId() + " y nombre: " + categoryDTO.getName());
                    category = createCategory(categoryDTO.getId(), categoryDTO.getName(), quizId);
                } else {                    
                    category = PersistedCategory.get();
                    System.out.println("GET: categoría existente - ID: " + category.getId() + " y nombre: " + category.getTitle());
                }	
                
                categories.add(category);
            }
        } else {
            System.out.println("No se pudieron obtener categorías desde la API.");
        }


        return categories;
    }

    @Transactional
    /**
     * Importa preguntas para una categoría específica desde la API de Open Trivia.
     * 
     * @param categoryQuestionCount Cantidad de preguntas totales a importar para la categoría.
     * @param category Categoría a la que pertenecen las preguntas.
     * @param quiz Quiz al que pertenecen las preguntas.
     */
    public void importQuestionsForCategory(int categoryQuestionCount, Category category, Quiz quiz) {
        int maxPerRequest = 50;
        int totalQuestionLoaded = 0;
        int responseCode = 0;
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Total a importar de la categoría " + category.getId() + " :" + categoryQuestionCount);

        while (responseCode == 0 && totalQuestionLoaded < categoryQuestionCount) {

            int amount = Math.min(maxPerRequest, categoryQuestionCount - totalQuestionLoaded);
            
            String url = String.format("/api.php?amount=%d&category=%d&token=%s", amount, category.getId(), token);
            OpenTriviaResponseDTO response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenTriviaResponseDTO.class)
                    .delayElement(Duration.ofSeconds(5L))
                    .block();
                    
            if (response == null) {
                System.out.println("No se pudo obtener la respuesta de la API. URL: " + url);
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

                        for (OpenTriviaQuestionDTO dto : response.getResults()) {
                            
                            // List<Answer> answers = new ArrayList<>();

                            // Answer correct = new Answer(dto.getCorrect_answer(), true);
                            // answers.add(correct);

                            // for (String wrong : dto.getIncorrect_answers()) {
                            //     Answer incorrect = new Answer(wrong, false);
                            //     answers.add(incorrect);
                            // }


                            // createQuestion(dto.getQuestion(), dto.getType(), Level.valueOf(dto.getDifficulty().toUpperCase()), category, quiz, answers);
                           
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
                        System.out.println("Se importaron " + totalQuestionLoaded);
                    break;
            }
        }
    }

    private int getCategoryQuestionCount(Long categoryId) {
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

    private Category createCategory(int id, String name, Long quizId) {
        Category category = new Category();
        category.setId((long) id);
        category.setTitle(name);
        category.setDescription(name);
        category.setEnable(true);
        category.setQuizId(quizId);

        categoryDao.save(category);

        return category;
    }
}

