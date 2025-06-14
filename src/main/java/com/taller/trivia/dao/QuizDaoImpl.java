package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.SessionException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Quiz;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
@Transactional
public class QuizDaoImpl implements QuizDao {

    @PersistenceContext
    private EntityManager entityManager;

        // Método para manejar errores de métodos que devuelven un valor
    private <T> T executeQuery(Supplier<T> function) {
        try {
            return function.get();
        } catch (SessionException e) {
            System.out.println("SessionException: " + e.getMessage());
            throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_CONNECTION_ERROR"));
        } catch (HibernateException e) {
            System.out.println("HibernateException: " + e.getMessage());
            throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException(ErrorMessageLoader.getMessage("SERVER_ERROR"));
        }
    }

    @Override
    public Quiz save(Quiz quiz) {
        return executeQuery(() -> {
            return entityManager.merge(quiz);
        });      
    }

    @Override
    public Quiz update(Long quizId, Quiz quiz) {
        return executeQuery(() -> {
            Quiz quizPersisted = entityManager.find(Quiz.class, quizId);

            if (quizPersisted == null) {
                throw new DatabaseException(ErrorMessageLoader.getMessage("QUIZ_NOT_FOUND", quizId));
            }
            
            quizPersisted.setName(quiz.getName());
            quizPersisted.setUrl(quiz.getUrl());

            return entityManager.merge(quizPersisted);            
        });
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Quiz quiz = entityManager.find(Quiz.class, id);

            if (quiz != null) {
                entityManager.remove(quiz);
            }

            return false;
        });        
    }

    @Override
    public Optional<Quiz> findById(Long id) {
        return executeQuery(() -> {
            return Optional.ofNullable(entityManager.find(Quiz.class, id));
        });        
    }

    @Override
    public List<Quiz> findAll() {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Quiz> criteriaQuery = criteriaBuilder.createQuery(Quiz.class);
            Root<Quiz> root = criteriaQuery.from(Quiz.class);
            criteriaQuery.select(root);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    }

    @Override
    public Optional<Quiz> findByName(String name) {
        return executeQuery(() -> {
            try {
                CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
                CriteriaQuery<Quiz> criteriaQuery = criteriaBuilder.createQuery(Quiz.class);
                Root<Quiz> root = criteriaQuery.from(Quiz.class);
                criteriaQuery.select(root);

                Predicate categoryPredicate = criteriaBuilder.equal(root.get("name"), name);
                criteriaQuery.where(categoryPredicate);
            
                return Optional.ofNullable(entityManager.createQuery(criteriaQuery).getSingleResult());
            } catch (jakarta.persistence.NoResultException e) {
                return Optional.empty();
            }
        });
    }

}
