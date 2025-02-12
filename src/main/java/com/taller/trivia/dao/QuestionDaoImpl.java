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

import com.taller.trivia.model.Question;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class QuestionDaoImpl implements QuestionDao {
        
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Question save(Question question) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.merge(question);
            return question;
            
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
            Question question = ctx.get(Question.class, id);

            if (question != null) {
                ctx.remove(question);
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
    public Optional<Question> findById(Long id) {
        
        try {
            Session ctx = sessionFactory.getCurrentSession();
            Question question = ctx.get(Question.class, id);

            if (question != null) {
                return Optional.of(question);
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
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<Question>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
            Root<Question> root = criteriaQuery.from(Question.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<Question> query = ctx.createQuery(criteriaQuery);
            questions = query.getResultList();

            return questions;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return questions;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return questions;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return questions;
        }
        
    }

    @Override
    public List<Question> findAllByCategory(Long id) {
        List<Question> questions = new ArrayList<Question>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
            Root<Question> root = criteriaQuery.from(Question.class);

            // Definir las condiciones de la consulta (en este caso, buscar por id)
            Predicate categoryPredicate = criteriaBuilder.equal(root.get("category").get("id"), id);
            criteriaQuery.select(root).where(categoryPredicate);

            // Ejecutar la consulta
            Query<Question> query = ctx.createQuery(criteriaQuery);
            questions = query.getResultList();

            return questions;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return questions;
        } catch(HibernateException e) {
            System.err.println("Error al obtener las preguntas de la categoría: " + e.getMessage());
            return questions;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return questions;
        }
        
    }

    @Override
    public List<Question> findAllQuestionsGame(Long gameId) {
        List<Question> questions = new ArrayList<Question>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
            Root<Question> root = criteriaQuery.from(Question.class);

            // Definir las condiciones de la consulta (en este caso, buscar por id)
            Predicate gamePredicate = criteriaBuilder.isMember(gameId, root.get("games"));
            criteriaQuery.select(root).where(gamePredicate);

            // Ejecutar la consulta
            Query<Question> query = ctx.createQuery(criteriaQuery);
            questions = query.getResultList();

            return questions;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return questions;
        } catch(HibernateException e) {
            System.err.println("Error al obtener las preguntas de una partida" + e.getMessage());
            return questions;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return questions;
        }
    };
}
