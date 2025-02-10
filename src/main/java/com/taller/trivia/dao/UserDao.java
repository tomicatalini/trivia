package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.model.User;

public interface UserDao {
    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findAllByRol(String rol);
    User save(User user);
    void delete(Long id);
}
