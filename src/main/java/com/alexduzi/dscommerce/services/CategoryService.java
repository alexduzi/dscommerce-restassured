package com.alexduzi.dscommerce.services;

import com.alexduzi.dscommerce.dto.CategoryDTO;
import com.alexduzi.dscommerce.entities.Category;
import com.alexduzi.dscommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> result = repository.findAll();

        return result.stream().map(CategoryDTO::new).toList();
    }
}
