package com.taller.trivia.dao;

import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.model.Answer;

public class AnswerDaoImpl implements AnswerDao{
 
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Answer save(Answer answer) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.save(answer);
            return answer;
            
        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return null;
        }catch(HibernateException e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }        
    }

    @Override
    public void delete(Long id) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            Answer answer = ctx.get(Answer.class, id);

            if (answer != null) {
                ctx.remove(answer);
            }
            
        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
        } catch(HibernateException e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
    }

    @Override
    public Optional<Answer> findById(Long id) {
        
        try {
            Session ctx = sessionFactory.getCurrentSession();
            Answer answer = ctx.get(Answer.class, id);

            if (answer != null) {
                return Optional.of(answer);
            } else {
                return Optional.empty();
            }

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return Optional.empty();
        } catch(HibernateException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return Optional.empty();
        }    
        
    }

}
