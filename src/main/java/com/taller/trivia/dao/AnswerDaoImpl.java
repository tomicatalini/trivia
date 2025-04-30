package com.taller.trivia.dao;

import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.SessionException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Answer;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class AnswerDaoImpl implements AnswerDao{
 
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
    public Answer save(Answer answer) {
        return executeQuery(() -> {
            return entityManager.merge(answer);
        });        
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Answer answer = entityManager.find(Answer.class, id);

            if (answer != null) {
                entityManager.remove(answer);
                return true;
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return executeQuery(() -> {
            return Optional.ofNullable(entityManager.find(Answer.class, id));
        });
    }
}
