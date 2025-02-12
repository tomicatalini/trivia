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

import com.taller.trivia.model.Answer;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class AnswerDaoImpl implements AnswerDao{
 
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Answer save(Answer answer) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.merge(answer);
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

    @Override
    public List<Answer> findAllQuestionAnswers(Long questionId) {
        List<Answer> answers = new ArrayList<Answer>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Answer> criteriaQuery = criteriaBuilder.createQuery(Answer.class);
            Root<Answer> root = criteriaQuery.from(Answer.class);

            // Consultar todos los usuarios
            Predicate questionPred = criteriaBuilder.equal(root.get("question").get("id"), questionId);
            criteriaQuery.select(root).where(questionPred);

            // Ejecutar la consulta
            Query<Answer> query = ctx.createQuery(criteriaQuery);
            answers = query.getResultList();

            return answers;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return answers;
        } catch(HibernateException e) {
            System.err.println("Error al obtener las respuestas de una pregunta: " + e.getMessage());
            return answers;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return answers;
        }
    }
}
