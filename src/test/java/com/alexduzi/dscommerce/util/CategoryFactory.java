package com.alexduzi.dscommerce.util;

import com.alexduzi.dscommerce.entities.Category;

public class CategoryFactory {

    public static Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Games");
        return category;
    }

    public static Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
