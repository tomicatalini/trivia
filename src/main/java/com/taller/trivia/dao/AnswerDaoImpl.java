package com.taller.trivia.dao;

import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Answer;
import com.taller.trivia.util.ErrorMessageLoader;

@Repository
public class AnswerDaoImpl implements AnswerDao{
 
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
    public Answer save(Answer answer) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return ctx.merge(answer);
        });        
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            Answer answer = ctx.get(Answer.class, id);

            if (answer != null) {
                ctx.remove(answer);
                return true;
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return Optional.ofNullable(ctx.get(Answer.class, id));
        });
    }
}
