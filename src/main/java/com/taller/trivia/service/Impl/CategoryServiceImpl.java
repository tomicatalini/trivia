package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taller.trivia.dao.CategoryDao;
import com.taller.trivia.dto.CategoryDTO;
import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.ServiceException;
import com.taller.trivia.model.Category;
import com.taller.trivia.service.CategoryService;
import com.taller.trivia.util.DTOMapper;
import com.taller.trivia.util.ErrorMessageLoader;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired 
    private CategoryDao repository;

    @Override
    public CategoryDTO save(CategoryDTO categoryDto) {
        
        try {
            if (categoryDto == null || categoryDto.getCategory() == null || categoryDto.getDescription() == null) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "titulo, descripcion"));
            }
            Category category = DTOMapper.toCategoryEntity(categoryDto);
            category = repository.save(category);
            return DTOMapper.toCategoryDTO(category);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }

    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDto) {
        
        try {
            Category category = DTOMapper.toCategoryEntity(categoryDto);
            category.setId(id);
            if (categoryDto == null || categoryDto.getCategory() == null || categoryDto.getDescription() == null) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "titulo, descripcion"));
            }
            // Save the updated category
            category = repository.save(category);
            return DTOMapper.toCategoryDTO(category);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }     

    }

    @Override
    public boolean delete(Long id) {
        try {
            if (!repository.delete(id)) {
                throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        try {
            return repository.findAll().stream()
                             .map(DTOMapper::toCategoryDTO)
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        try {
            return repository.findById(id)
                             .map(DTOMapper::toCategoryDTO)
                             .orElse(null);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
                                 
    }

    @Override
    public List<CategoryDTO> getAllQuizCategories(Long quizId) {
        try {
            return repository.findAll().stream()
                             .map(DTOMapper::toCategoryDTO)
                             .filter(category -> category.getQuizId().equals(quizId))
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

}
