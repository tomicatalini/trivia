package com.taller.trivia.service;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.model.User;

public interface UserService {

    List<User> getAll();
    Optional<User> getById(Long id);
    Optional<User> getByName(String name);
    Optional<User> getByEmail(String email);
    User save(User user);
    void delete(Long id);
}
