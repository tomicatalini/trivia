package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Question;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.model.Level; // Import the Level enum

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class QuestionDaoImpl implements QuestionDao {
        
    @Autowired
    private EntityManager entityManager;

    // Método para manejar errores de métodos que devuelven un valor
    private <T> T executeQuery(Supplier<T> function) {
        try {
            return function.get();
        } catch (SessionException e) {
            throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_CONNECTION_ERROR"));
        } catch (HibernateException e) {
            throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessageLoader.getMessage("SERVER_ERROR"));
        }
    }

    @Override
    public Question save(Question question) {
        return executeQuery(() -> {
            return entityManager.merge(question);
        });        
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Question question = entityManager.find(Question.class, id);

            if (question != null) {
                entityManager.remove(question);
                return true;
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Question> findById(Long id) {
        return executeQuery(() -> {
            return Optional.ofNullable(entityManager.find(Question.class, id));
        });        
    }

    @Override
    public List<Question> findAll() {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
            Root<Question> root = criteriaQuery.from(Question.class);

            //root.fetch("answers", JoinType.LEFT);
            criteriaQuery.select(root);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });        
    }

    @Override
    public List<Question> findAllByCategory(Long id) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
            Root<Question> root = criteriaQuery.from(Question.class);

            //Filtro por categoria
            Predicate categoryPredicate = criteriaBuilder.equal(root.get("category").get("id"), id);
            criteriaQuery.select(root).where(categoryPredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });        
    }

    @Override
    public List<Question> findAllQuestionsGame(Long gameId) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
            Root<Question> root = criteriaQuery.from(Question.class);

            //Filtro por partida
            Predicate gamePredicate = criteriaBuilder.isMember(gameId, root.get("games"));
            criteriaQuery.select(root).where(gamePredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    };

    @Override
    public List<Question> findRandomQuestions(Long categoryId, Long quizId, String level_String, int limit) {
        if (level_String.isBlank()) {
            String sql = "SELECT * FROM question " +
                     "WHERE category_id = :categoryId AND quiz_id = :quizId " +
                     "ORDER BY RAND() LIMIT :limit";

            return executeQuery(() -> {
                return entityManager.createNativeQuery(sql, Question.class)
                        .setParameter("categoryId", categoryId)
                        .setParameter("quizId", quizId)
                        .setParameter("limit", limit)
                        .getResultList();
            });
        } else {
            String sql = "SELECT * FROM question " +
                     "WHERE category_id = :categoryId AND quiz_id = :quizId AND level = :levelValue " +
                     "ORDER BY RAND() LIMIT :limit";

            return executeQuery(() -> {
                return entityManager.createNativeQuery(sql, Question.class)
                        .setParameter("categoryId", categoryId)
                        .setParameter("quizId", quizId)
                        .setParameter("levelValue", Level.valueOf(level_String))
                        .setParameter("limit", limit)
                        .getResultList();
            });
        }
        
    }
}
