package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.taller.trivia.dao.UserDao;
import com.taller.trivia.model.User;
import com.taller.trivia.service.UserService;

public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<User> getByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<User> getByEmail(String mail) {
        return repository.findByName(mail);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
