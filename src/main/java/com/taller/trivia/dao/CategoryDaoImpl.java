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
import com.taller.trivia.model.Category;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class CategoryDaoImpl implements CategoryDao{
        
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
    public Category save(Category category) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return ctx.merge(category);
        });          
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            Category category = ctx.get(Category.class, id);

            if (category != null) {
                ctx.remove(category);
                return true;
            }
            
            return false;
        });        
    }

    @Override
    public Optional<Category> findById(Long id) {        
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            return Optional.ofNullable(ctx.get(Category.class, id));
        });
    }

    @Override
    public List<Category> findAll() {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);
            criteriaQuery.select(root);

            return ctx.createQuery(criteriaQuery).getResultList();
        });        
    }

    @Override
    public List<Category> findAllByState(Boolean state) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);
            
            //Filtro aquellas categoria cone estado "habilitado"
            Predicate categoryPredicate = criteriaBuilder.equal(root.get("enable"), state);
            criteriaQuery.select(root).where(categoryPredicate);

            return ctx.createQuery(criteriaQuery).getResultList();
        });        
    }

    @Override
    public List<Category> findAllByTitle(String title) {
        return executeQuery(() -> {
            Session ctx = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);
            
            //Filtro por categoria
            Predicate categoryPredicate = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            criteriaQuery.select(root).where(categoryPredicate);

            return ctx.createQuery(criteriaQuery).getResultList();
        });        
    }
}
