package com.alexduzi.dscommerce.services;

import com.alexduzi.dscommerce.dto.CategoryDTO;
import com.alexduzi.dscommerce.entities.Category;
import com.alexduzi.dscommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static com.alexduzi.dscommerce.util.CategoryFactory.createCategory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private List<Category> categories;

    @BeforeEach
    void setUp() throws Exception {
        category = createCategory();
        categories = new ArrayList<>();
        categories.add(category);

        when(categoryRepository.findAll()).thenReturn(categories);
    }

    @Test
    void shouldReturnFindAllCategories() {
        List<CategoryDTO> result = categoryService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(categories.size(), result.size());
        assertEquals(categories.get(0).getId(), result.get(0).getId());
        assertEquals(categories.get(0).getName(), result.get(0).getName());
    }
}