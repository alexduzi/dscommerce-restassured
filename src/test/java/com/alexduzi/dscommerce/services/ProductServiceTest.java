package com.alexduzi.dscommerce.services;

import com.alexduzi.dscommerce.dto.ProductDTO;
import com.alexduzi.dscommerce.dto.ProductMinDTO;
import com.alexduzi.dscommerce.entities.Category;
import com.alexduzi.dscommerce.entities.Product;
import com.alexduzi.dscommerce.repositories.ProductRepository;
import com.alexduzi.dscommerce.services.exceptions.DatabaseException;
import com.alexduzi.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.alexduzi.dscommerce.util.ProductFactory.createProduct;
import static com.alexduzi.dscommerce.util.ProductFactory.createProductDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository repository;

    @Spy
    private ModelMapper modelMapper;

    private Product product;
    private ProductDTO productDTO;
    private Long existingId;
    private Long nonExistingId;

    private Product pageProduct;
    private PageImpl<Product> pageResult;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;

        product = new Product(existingId, "Playstation", "video game", 2000.0, "url");
        product.getCategories().add(new Category(1L, "Category 1"));
        productDTO = new ProductDTO(product);

        pageProduct = new Product(3L, "Playstation 5", "video game", 3000.0, "url");
        pageResult = new PageImpl<>(List.of(pageProduct));

        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);
        when(repository.save(any())).thenReturn(product);
        when(repository.getReferenceById(existingId)).thenReturn(product);
        when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(repository.searchByName(any(), (Pageable) any())).thenReturn(pageResult);
    }

    @Test
    void shouldProperlyFindProductById() {
        Product productFindById = new Product(existingId, "Playstation", "video game", 2000.0, "url");
        when(repository.findById(existingId)).thenReturn(Optional.of(productFindById));

        ProductDTO result = productService.findById(existingId);

        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
    }

    @Test
    void shouldProductServiceReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
        when(repository.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }

    @Test
    void shouldProductServiceSaveProduct() {
        ProductService serviceSpy = spy(productService);
        doNothing().when(serviceSpy).validateData(productDTO);

        ProductDTO result = serviceSpy.insert(productDTO);

        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
    }

    @Test
    void insertShouldReturnIllegalArgumentExceptionWhenProductNameIsBlank() {
        productDTO.setName(null);

        ProductService serviceSpy = spy(productService);
        doThrow(IllegalArgumentException.class).when(serviceSpy).validateData(productDTO);

        assertThrows(IllegalArgumentException.class, () -> serviceSpy.insert(productDTO));
    }

    @Test
    void insertShouldReturnIllegalArgumentExceptionWhenProductPriceIsNegative() {
        productDTO.setPrice(-5.0);

        ProductService serviceSpy = spy(productService);
        doThrow(IllegalArgumentException.class).when(serviceSpy).validateData(productDTO);

        assertThrows(IllegalArgumentException.class, () -> serviceSpy.insert(productDTO));
    }

    @Test
    void updateShouldReturnProductDTOWhenProductExists() {
        ProductService serviceSpy = spy(productService);
        doNothing().when(serviceSpy).validateData(productDTO);

        ProductDTO result = serviceSpy.update(existingId, productDTO);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
    }

    @Test
    void updateShouldReturnIllegalArgumentExceptionWhenProductExistsAndNameIsBlank() {
        productDTO.setName(null);

        ProductService serviceSpy = spy(productService);
        doThrow(IllegalArgumentException.class).when(serviceSpy).validateData(productDTO);

        assertThrows(IllegalArgumentException.class, () -> serviceSpy.update(existingId, productDTO));
    }

    @Test
    void updateShouldReturnIllegalArgumentExceptionWhenProductExistsAndPriceIsNegative() {
        productDTO.setPrice(-5.0);

        ProductService serviceSpy = spy(productService);
        doThrow(IllegalArgumentException.class).when(serviceSpy).validateData(productDTO);

        assertThrows(IllegalArgumentException.class, () -> serviceSpy.update(existingId, productDTO));
    }

    @Test
    void updateShouldReturnIllegalArgumentExceptionWhenProductDontExists() {
        ProductService serviceSpy = spy(productService);
        doNothing().when(serviceSpy).validateData(productDTO);

        assertThrows(ResourceNotFoundException.class, () -> serviceSpy.update(nonExistingId, productDTO));
    }

    @Test
    void findAllShouldReturnPagedProductMinDTO() {
        Pageable pageable = PageRequest.of(0, 12);
        String name = "Playstation 5";

        ProductService serviceSpy = spy(productService);

        Page<ProductMinDTO> result = serviceSpy.findAll(name, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateShouldReturnProductUpdated() {
        Long prodId = 10L;
        Product productUpdateRefId = createProduct(prodId, "Playstation 5", "video game", 2000.0, "url");
        Product productUpdated = createProduct(prodId, "Playstation 5 updated", "video game", 2000.0, "url");
        ProductDTO productUpdateDTO = createProductDTO(productUpdated);

        when(repository.getReferenceById(prodId)).thenReturn(productUpdateRefId);
        when(repository.save(any())).thenReturn(productUpdated);

        ProductDTO result = productService.update(prodId, productUpdateDTO);

        assertNotNull(result);
        assertEquals(productUpdateDTO.getName(), result.getName());
    }

    @Test
    void deleteShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
        when(repository.existsById(existingId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.delete(nonExistingId));
    }

    @Test
    void deleteShouldReturnDataIntegrityViolationExceptionWhenIdHaveCascadingConstraints() {
        when(repository.existsById(existingId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(existingId);

        assertThrows(DatabaseException.class, () -> productService.delete(existingId));
    }

    @Test
    void deleteShouldRemoveProductWithoutException() {
        when(repository.existsById(existingId)).thenReturn(true);
        doNothing().when(repository).deleteById(existingId);

        assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });
    }
}