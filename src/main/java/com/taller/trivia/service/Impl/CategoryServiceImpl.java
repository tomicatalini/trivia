package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.trivia.model.Category;
import com.taller.trivia.dao.CategoryDao;
import com.taller.trivia.dto.CategoryDTO;
import com.taller.trivia.service.CategoryService;
import com.taller.trivia.util.DTOMapper;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired 
    private CategoryDao repository;

    @Override
    public CategoryDTO save(CategoryDTO categoryDto) {
        if (categoryDto == null) {
            throw new IllegalArgumentException("El objeto categoryDto no puede ser nulo");
        }

        if (categoryDto.getTitle() == null) {
            throw new IllegalArgumentException("El campo 'name' es obligatorio");
        }

        Category category = DTOMapper.toCategoryEntity(categoryDto);

        category = repository.save(category);
        return DTOMapper.toCategoryDTO(category);
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDto) {
        if (categoryDto == null) {
            throw new IllegalArgumentException("El objeto categoryDto no puede ser nulo");
        }

        if (categoryDto.getTitle() == null) {
            throw new IllegalArgumentException("El campo 'name' es obligatorio");
        }

        Category category = DTOMapper.toCategoryEntity(categoryDto);
        category.setId(id);

        category = repository.save(category);
        return DTOMapper.toCategoryDTO(category);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return repository.findAll().stream()
                         .map(DTOMapper::toCategoryDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return repository.findById(id)
                         .map(DTOMapper::toCategoryDTO)
                         .orElse(null);
    }

}
