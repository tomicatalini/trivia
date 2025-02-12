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

import com.taller.trivia.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User save(User user) {
        try {
            Session ctx = sessionFactory.getCurrentSession();
            ctx.merge(user);
            return user;
            
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
            User user = ctx.get(User.class, id);

            if (user != null) {
                ctx.remove(user);
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
    public Optional<User> findById(Long id) {
        
        try {
            Session ctx = sessionFactory.getCurrentSession();
            User user = ctx.get(User.class, id);

            if (user != null) {
                return Optional.of(user);
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
    public List<User> findAll() {
        List<User> users = new ArrayList<User>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<User> query = ctx.createQuery(criteriaQuery);
            users = query.getResultList();

            return users;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return users;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return users;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return users;
        }
        
    }

    @Override
    public List<User> findAllByRol(String rol) {
        List<User> users = new ArrayList<User>();
        
        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Definir las condiciones de la consulta (en este caso, buscar por nombre)
            Predicate rolPredicate = criteriaBuilder.equal(root.get("rol"), rol);
            criteriaQuery.select(root).where(rolPredicate);

            // Ejecutar la consulta
            Query<User> query = ctx.createQuery(criteriaQuery);
            users = query.getResultList();

            return users;

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return users;
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario por rol: " + e.getMessage());
            return users;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return users;
        }
        
    }

    @Override
    public Optional<User> findByName(String name) {

        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            // Definir las condiciones de la consulta (en este caso, buscar por rol)
            Predicate namePredicate = criteriaBuilder.equal(root.get("name"), name);
            criteriaQuery.select(root).where(namePredicate);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<User> query = ctx.createQuery(criteriaQuery);
            User user = (User) query.uniqueResult();
            
            return Optional.of(user);

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return Optional.empty();
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return Optional.empty();
        }
        
    }

    @Override
    public Optional<User> findByEmail(String email) {

        try {
            Session ctx = sessionFactory.getCurrentSession();

            // Crear el CriteriaBuilder y CriteriaQuery
            CriteriaBuilder criteriaBuilder = ctx.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            // Definir las condiciones de la consulta (en este caso, buscar por nombre)
            Predicate emailPredicate = criteriaBuilder.equal(root.get("name"), email);
            criteriaQuery.select(root).where(emailPredicate);

            // Consultar todos los usuarios
            criteriaQuery.select(root);

            // Ejecutar la consulta
            Query<User> query = ctx.createQuery(criteriaQuery);
            User user = (User) query.uniqueResult();
            
            return Optional.of(user);

        } catch(SessionException e){
            System.err.println("Error al obtener conexión con la db: " + e.getMessage());
            return Optional.empty();
        } catch(HibernateException e) {
            System.err.println("Error al obtener el usuario: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return Optional.empty();
        }
        
    }
}
