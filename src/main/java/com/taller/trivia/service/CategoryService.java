package com.taller.trivia.service;

import java.util.List;

import com.taller.trivia.dto.CategoryDTO;

public interface CategoryService {

    public CategoryDTO save(CategoryDTO category);
    public CategoryDTO update(Long id, CategoryDTO category);
    public void delete(Long id);    
    public List<CategoryDTO> getAllCategories();
    public CategoryDTO getCategoryById(Long id);

}
