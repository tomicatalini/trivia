package com.taller.trivia.dao;

import java.util.List;
import java.util.Optional;

import com.taller.trivia.model.Category;

public interface CategoryDao {
    
    Category save(Category category);
    boolean delete(Long id);
    Optional<Category> findById(Long id);
    Optional<Category> findByTitle(String title);
    List<Category> findAllByState(Boolean state);
    List<Category> findAllByTitle(String title);
    List<Category> findAll();
}
