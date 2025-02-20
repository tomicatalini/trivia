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
import com.taller.trivia.model.User;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Método para manejar errores de métodos que devuelven un valor
    private <T> T executeQuery(Supplier<T> function) {
        try {            
            return function.get();
        } catch (SessionException e) {
            System.out.println("Entro al SessionException (DAO)");
            throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_CONNECTION_ERROR"));
        } catch (HibernateException e) {
            System.out.println("Entro al HibernateException (DAO)");
            throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        } catch (Exception e) {
            System.out.println("Entro al Exception (DAO)");
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User save(User user) {
        return executeQuery(() -> {
            return entityManager.merge(user);
        });      
    }

    @Override
    public User update(Long userId, User user) {
        return executeQuery(() -> {
            User userPersisted = entityManager.find(User.class, userId);

            if (userPersisted == null) {
                throw new DatabaseException(ErrorMessageLoader.getMessage("USER_NOT_FOUND", userId));
            }
            
            userPersisted.setName(user.getName());
            userPersisted.setEmail(user.getEmail());
            userPersisted.setRol(user.getRol());

            return entityManager.merge(userPersisted); 
            
        });
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            User user = entityManager.find(User.class, id);

            if (user != null) {
                entityManager.remove(user);
                return true;
            }
            
            return false;
        });   
    }

    @Override
    public Optional<User> findById(Long id) {
        return executeQuery(() -> {
            return Optional.ofNullable(entityManager.find(User.class, id));
        }); 
    }

    @Override
    public List<User> findAll() {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });   
    }

    @Override
    public List<User> findAllByRol(String rol) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);

            Predicate rolPredicate = criteriaBuilder.equal(root.get("rol"), rol);
            criteriaQuery.select(root).where(rolPredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });     
    }

    @Override
    public Optional<User> findByName(String name) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            Predicate namePredicate = criteriaBuilder.equal(root.get("name"), name);
            criteriaQuery.select(root).where(namePredicate);
            
            return Optional.ofNullable(entityManager.createQuery(criteriaQuery).getSingleResult());
        });        
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            Predicate emailPredicate = criteriaBuilder.equal(root.get("email"), email);
            criteriaQuery.select(root).where(emailPredicate);

            return Optional.ofNullable(entityManager.createQuery(criteriaQuery).getSingleResult());
        });        
    }
}
