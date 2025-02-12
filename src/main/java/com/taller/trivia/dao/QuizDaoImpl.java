package com.taller.trivia.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.model.Quiz;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;


public class QuizDaoImpl implements QuizDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Quiz save(Quiz quiz) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.merge(quiz);
            return quiz;
            
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
            Quiz quiz = ctx.get(Quiz.class, id);

            if (quiz != null) {
                ctx.remove(quiz);
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
    public Optional<Quiz> findById(Long id) {
        
        try {
            Session ctx = sessionFactory.getCurrentSession();
            Quiz quiz = ctx.get(Quiz.class, id);

            if (quiz != null) {
                return Optional.of(quiz);
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

    @Override
    public List<Quiz> findAll() {
        List<Quiz> quizies = new ArrayList<Quiz>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Quiz> criteriaQuery = criteriaBuilder.createQuery(Quiz.class);
            Root<Quiz> root = criteriaQuery.from(Quiz.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<Quiz> query = ctx.createQuery(criteriaQuery);
            quizies = query.getResultList();

            return quizies;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return quizies;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return quizies;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return quizies;
        }
        
    }

}
