package com.taller.trivia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.trivia.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    //Aca van los metodos del repositorio: por ejemplo, un GetAll()
}
