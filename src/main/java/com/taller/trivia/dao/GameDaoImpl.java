package com.taller.trivia.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.model.Game;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

public class GameDaoImpl  implements GameDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Game save(Game game) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.merge(game);
            return game;
            
        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return null;
        }catch(HibernateException e) {
            System.err.println("Error al guardar la partida: " + e.getMessage());
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
            Game game = ctx.get(Game.class, id);

            if (game != null) {
                ctx.remove(game);
            }
            
        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
        } catch(HibernateException e) {
            System.err.println("Error al eliminar la partida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
    }

    @Override
    public Optional<Game> findById(Long id) {
        
        try {
            Session ctx = sessionFactory.getCurrentSession();
            Game game = ctx.get(Game.class, id);

            if (game != null) {
                return Optional.of(game);
            } else {
                return Optional.empty();
            }

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return Optional.empty();
        } catch(HibernateException e) {
            System.err.println("Error al obtener la partida: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return Optional.empty();
        }    
        
    }

    @Override
    public List<Game> findAll() {
        List<Game> games = new ArrayList<Game>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<Game> query = ctx.createQuery(criteriaQuery);
            games = query.getResultList();

            return games;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return games;
        } catch(HibernateException e) {
            System.err.println("Error al obtener las partidas: " + e.getMessage());
            return games;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return games;
        }
        
    }
    
    @Override
    public List<Game> findAllUserGames(Long userId) {
        List<Game> games = new ArrayList<Game>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Consultar todos los usuarios
            Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
            criteriaQuery.select(root).where(userPredicate);

            // Ejecutar la consulta
            Query<Game> query = ctx.createQuery(criteriaQuery);
            games = query.getResultList();

            return games;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return games;
        } catch(HibernateException e) {
            System.err.println("Error al obtener las partidas del usuario: " + e.getMessage());
            return games;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return games;
        }
    };

    @Override
    public List<Game> findTopScores(Integer count) {
        List<Game> games = new ArrayList<Game>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("score")));

            // Ejecutar la consulta
            Query<Game> query = ctx.createQuery(criteriaQuery)
                                   .setMaxResults(count);

            games = query.getResultList();

            return games;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return games;
        } catch(HibernateException e) {
            System.err.println("Error al obtener top de puntajes: " + e.getMessage());
            return games;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return games;
        }
    };

    @Override
    public List<Game> findByDates(Date starDate, Date endDate) {
        List<Game> games = new ArrayList<Game>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            List<Predicate> predicates = new ArrayList<>();

            // Consultar por fechas
            if (starDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), starDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDate));
            }

            if (!predicates.isEmpty()) {
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            }            

            // Ejecutar la consulta
            Query<Game> query = ctx.createQuery(criteriaQuery);

            games = query.getResultList();

            return games;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return games;
        } catch(HibernateException e) {
            System.err.println("Error al obtener las partidas por fechas: " + e.getMessage());
            return games;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return games;
        }
    };

    @Override
    public List<Game> findByQuiz(Long quizId) {
        List<Game> games = new ArrayList<Game>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Consultar todos los usuarios
            Predicate quizPredicate = criteriaBuilder.equal(root.get("quiz").get("id"), quizId);
            criteriaQuery.select(root).where(quizPredicate);

            // Ejecutar la consulta
            Query<Game> query = ctx.createQuery(criteriaQuery);
            games = query.getResultList();

            return games;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return games;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el conjunto de la partida: " + e.getMessage());
            return games;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return games;
        }
    };
}
