package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.User;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserDaoImpl implements UserDao {

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

    // // Método para manejar errores de métodos que no devuelven nada (void)
    // private void executeVoidQuery(Runnable function) {
    //     try {
    //         function.run();
    //     } catch (SessionException e) {
    //         throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_CONNECTION_ERROR"));
    //     } catch (HibernateException e) {
    //         throw new DatabaseException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
    //     } catch (Exception e) {
    //         throw new RuntimeException(ErrorMessageLoader.getMessage("SERVER_ERROR"));
    //     }
    // }

    @Override
    public User save(User user) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return ctx.merge(user);
        });      
    }

    @Override
    public User update(Long userId, User user) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            User userPersisted = ctx.get(User.class, userId);

            if (userPersisted == null) {
                throw new DatabaseException(ErrorMessageLoader.getMessage("USER_NOT_FOUND", userId));
            }

            
            userPersisted.setName(user.getName());
            userPersisted.setEmail(user.getEmail());

            return ctx.merge(userPersisted); 
            
        });
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            User user = ctx.get(User.class, id);

            if (user != null) {
                ctx.remove(user);
                return true;
            }
            
            return false;
        });   
    }

    @Override
    public Optional<User> findById(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return Optional.ofNullable(ctx.get(User.class, id));
        }); 
    }

    @Override
    public List<User> findAll() {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);

            return ctx.createQuery(criteriaQuery).getResultList();
        });   
    }

    @Override
    public List<User> findAllByRol(String rol) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);

            Predicate rolPredicate = criteriaBuilder.equal(root.get("rol"), rol);
            criteriaQuery.select(root).where(rolPredicate);

            return ctx.createQuery(criteriaQuery).getResultList();
        });     
    }

    @Override
    public Optional<User> findByName(String name) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            Predicate namePredicate = criteriaBuilder.equal(root.get("name"), name);
            criteriaQuery.select(root).where(namePredicate);
            
            return Optional.ofNullable(ctx.createQuery(criteriaQuery).getSingleResult());
        });        
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            Predicate emailPredicate = criteriaBuilder.equal(root.get("email"), email);
            criteriaQuery.select(root).where(emailPredicate);

            return Optional.ofNullable(ctx.createQuery(criteriaQuery).getSingleResult());
        });        
    }
}
