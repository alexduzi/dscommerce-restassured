package com.alexduzi.dscommerce.util;

import com.alexduzi.dscommerce.dto.ProductDTO;
import com.alexduzi.dscommerce.entities.Category;
import com.alexduzi.dscommerce.entities.Product;

public class ProductFactory {

    public static Product createProduct() {
        return new Product(5L, "Playstation", "video game", 2000.0, "url");
    }

    public static Product createProduct(Long id, String name, String description, double price, String url) {
        Product product = new Product(id, name, description, price, url);
        product.getCategories().add(new Category(1L, "Category 1"));
        return product;
    }

    public static ProductDTO createProductDTO(Product product) {
        return new ProductDTO(product);
    }
}
