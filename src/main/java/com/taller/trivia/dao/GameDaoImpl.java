package com.taller.trivia.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Game;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

@Repository
public class GameDaoImpl  implements GameDao {

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
    public Game save(Game game) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return ctx.merge(game);
        });          
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            Game game = ctx.get(Game.class, id);

            if (game != null) {
                ctx.remove(game);
                return true;
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Game> findById(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return Optional.ofNullable(ctx.get(Game.class, id));
        });
    }

    @Override
    public List<Game> findAll() {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);
            criteriaQuery.select(root);

            return ctx.createQuery(criteriaQuery).getResultList();
        });        
    }
    
    @Override
    public List<Game> findAllUserGames(Long userId) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Filtro por usuario
            Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
            criteriaQuery.select(root).where(userPredicate);

            return ctx.createQuery(criteriaQuery).getResultList();
        });
    };

    @Override
    public List<Game> findTopScores(Integer count) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Filtro por usuario
            criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("score")));

            return ctx.createQuery(criteriaQuery)
                      .setMaxResults(count)
                      .getResultList();
        });
    };

    @Override
    public List<Game> findByDates(Date starDate, Date endDate) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            List<Predicate> predicates = new ArrayList<>();

            //Valido los parámetros
            if (starDate == null && endDate == null){
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_INVALID_DATE"));
            }

            // Consultar por fechas
            if (starDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), starDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDate));
            }

            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            return ctx.createQuery(criteriaQuery).getResultList();
        });
    };

    @Override
    public List<Game> findByQuiz(Long quizId) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Filtro por quiz
            Predicate quizPredicate = criteriaBuilder.equal(root.get("quiz").get("id"), quizId);
            criteriaQuery.select(root).where(quizPredicate);

            return ctx.createQuery(criteriaQuery).getResultList();
        });
    };
}
