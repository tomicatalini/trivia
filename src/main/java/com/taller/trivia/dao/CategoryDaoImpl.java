package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.model.Category;
import com.taller.trivia.util.ErrorMessageLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
@Transactional // Asegura que todos los métodos de esta clase estén dentro de una transacción
public class CategoryDaoImpl implements CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Método para manejar errores de métodos que devuelven un valor
    private <T> T executeQuery(Supplier<T> function) {
        try {
            return function.get();
        } catch (Exception e) {
            System.out.println("SessionException: " + e.getMessage());
            throw new DatabaseException(ErrorMessageLoader.getMessage("SERVER_ERROR"));
        }
    }

    @Override
    public Category save(Category category) {
        return executeQuery(() -> {
            return entityManager.merge(category);
        });
    }

    @Override
    public boolean delete(Long id) {
        return executeQuery(() -> {
            Category category = entityManager.find(Category.class, id);
            if (category != null) {
                entityManager.remove(category);
                return true;
            }
            return false;
        });
    }

    @Override
    public Optional<Category> findById(Long id) {
        return executeQuery(() -> {
            return Optional.ofNullable(entityManager.find(Category.class, id));
        });
    }

    @Override
    public List<Category> findAll() {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);
            criteriaQuery.select(root);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    }

    @Override
    public List<Category> findAllByState(Boolean state) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);

            // Filtro aquellas categorías con estado "habilitado"
            Predicate categoryPredicate = criteriaBuilder.equal(root.get("enable"), state);
            criteriaQuery.select(root).where(categoryPredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    }

    @Override
    public List<Category> findAllByTitle(String title) {
        return executeQuery(() -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);

            // Filtro por categoría
            Predicate categoryPredicate = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            criteriaQuery.select(root).where(categoryPredicate);

            return entityManager.createQuery(criteriaQuery).getResultList();
        });
    }
}
