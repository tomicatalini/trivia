package com.taller.trivia.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.SessionException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Game;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

@Repository
@Transactional
public class GameDaoImpl  implements GameDao {

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
    public Game save(Game game) {
        return executeQuery(() -> {
            return entityManager.merge(game);
        });          
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Game game = entityManager.find(Game.class, id);

            if (game != null) {
                entityManager.remove(game);
                return true;
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Game> findById(Long id) {
        return executeQuery(() -> {
            return Optional.ofNullable(entityManager.find(Game.class, id));
        });
    }

    @Override
    public List<Game> findAll() {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);
            criteriaQuery.select(root);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });        
    }
    
    @Override
    public List<Game> findAllUserGames(Long userId) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Filtro por usuario
            Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
            criteriaQuery.select(root).where(userPredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    };

    @Override
    public List<Game> findTopRanking(int number) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            criteriaQuery.select(root)
                .where(criteriaBuilder.isNotNull(root.get("score")))
                .orderBy(criteriaBuilder.desc(root.get("score")));

            return entityManager.createQuery(criteriaQuery)
                                .setMaxResults(number)
                                .getResultList();
        });
    };

    @Override
    public List<Game> findByDates(Date starDate, Date endDate) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
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

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    };

    @Override
    public List<Game> findByQuiz(Long quizId) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            // Filtro por quiz
            Predicate quizPredicate = criteriaBuilder.equal(root.get("quiz").get("id"), quizId);
            criteriaQuery.select(root).where(quizPredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    };
}
