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
import com.taller.trivia.model.Quiz;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class QuizDaoImpl implements QuizDao {

    @Autowired
    private SessionFactory sessionFactory;

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
    public Quiz save(Quiz quiz) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return ctx.merge(quiz);
        });      
    }

    @Override
    public Quiz update(Long quizId, Quiz quiz) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            Quiz quizPersisted = ctx.get(Quiz.class, quizId);

            if (quizPersisted == null) {
                throw new DatabaseException(ErrorMessageLoader.getMessage("QUIZ_NOT_FOUND", quizId));
            }

            
            quizPersisted.setName(quiz.getName());
            quizPersisted.setUrl(quiz.getUrl());

            return ctx.merge(quizPersisted);            
        });
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            Quiz quiz = ctx.get(Quiz.class, id);

            if (quiz != null) {
                ctx.remove(quiz);
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Quiz> findById(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return Optional.ofNullable(ctx.get(Quiz.class, id));
        });        
    }

    @Override
    public List<Quiz> findAll() {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Quiz> criteriaQuery = criteriaBuilder.createQuery(Quiz.class);
            Root<Quiz> root = criteriaQuery.from(Quiz.class);
            criteriaQuery.select(root);

            return ctx.createQuery(criteriaQuery).getResultList();
        });
    }

}
