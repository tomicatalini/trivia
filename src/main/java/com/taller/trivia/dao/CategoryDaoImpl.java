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

import com.taller.trivia.model.Category;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CategoryDaoImpl implements CategoryDao{
        
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Category save(Category category) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.merge(category);
            return category;
            
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
            Category category = ctx.get(Category.class, id);

            if (category != null) {
                ctx.remove(category);
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
    public Optional<Category> findById(Long id) {
        
        try {
            Session ctx = sessionFactory.getCurrentSession();
            Category category = ctx.get(Category.class, id);

            if (category != null) {
                return Optional.of(category);
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
    public List<Category> findAllByState(Boolean state) {
        List<Category> categories = new ArrayList<Category>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);

            // Definir las condiciones de la consulta (en este caso, buscar por habilitado)
            Predicate categoryPredicate = criteriaBuilder.equal(root.get("enable"), state);
            criteriaQuery.select(root).where(categoryPredicate);

            // Ejecutar la consulta
            Query<Category> query = ctx.createQuery(criteriaQuery);
            categories = query.getResultList();

            return categories;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return categories;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return categories;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return categories;
        }
        
    }

    @Override
    public List<Category> findAllByTitle(String title) {
        List<Category> categories = new ArrayList<Category>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);

            // Definir las condiciones de la consulta (en este caso, buscar por habilitado)
            Predicate categoryPredicate = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            criteriaQuery.select(root).where(categoryPredicate);

            // Ejecutar la consulta
            Query<Category> query = ctx.createQuery(criteriaQuery);
            categories = query.getResultList();

            return categories;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return categories;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return categories;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return categories;
        }
        
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<Category>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<Category> query = ctx.createQuery(criteriaQuery);
            categories = query.getResultList();

            return categories;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return categories;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return categories;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return categories;
        }
        
    }

}
