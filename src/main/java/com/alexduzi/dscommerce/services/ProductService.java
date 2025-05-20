package com.alexduzi.dscommerce.services;

import com.alexduzi.dscommerce.dto.CategoryDTO;
import com.alexduzi.dscommerce.dto.ProductDTO;
import com.alexduzi.dscommerce.dto.ProductMinDTO;
import com.alexduzi.dscommerce.entities.Category;
import com.alexduzi.dscommerce.entities.Product;
import com.alexduzi.dscommerce.repositories.ProductRepository;
import com.alexduzi.dscommerce.services.exceptions.DatabaseException;
import com.alexduzi.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));

        return convertToDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        Page<Product> product = repository.searchByName(name, pageable);

        return product.map(this::convertToMinDto);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        validateData(dto);
        Product product = convertToEntity(dto);
        product = repository.save(product);
        return convertToDto(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            validateData(dto);
            Product product = repository.getReferenceById(id);
            copyDtoToEntity(dto, product);
            product = repository.save(product);
            return convertToDto(product);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    protected void validateData(ProductDTO productDto) {
        if (productDto.getName() == null || productDto.getName().isBlank()) {
            throw new IllegalArgumentException("Field name cannot be blank");
        }
        if (productDto.getPrice() == null || productDto.getPrice() <= 0) {
            throw new IllegalArgumentException("Field price cannot be zero or negative");
        }
    }

    private ProductDTO convertToDto(Product product) {
        ProductDTO productDto = modelMapper.map(product, ProductDTO.class);
        productDto.getCategories().addAll(product.getCategories().stream().map(x -> modelMapper.map(x, CategoryDTO.class)).toList());
        return productDto;
    }

    private ProductMinDTO convertToMinDto(Product product) {
        return modelMapper.map(product, ProductMinDTO.class);
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        product.getCategories().addAll(productDTO.getCategories().stream().map(x -> modelMapper.map(x, Category.class)).toList());
        return product;
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()) {
            Category cat = new Category();
            cat.setId(catDto.getId());
            entity.getCategories().add(cat);
        }
    }
}
