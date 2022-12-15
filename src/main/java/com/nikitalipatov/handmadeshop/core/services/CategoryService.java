package com.nikitalipatov.handmadeshop.core.services;

import com.nikitalipatov.handmadeshop.core.models.Category;
import com.nikitalipatov.handmadeshop.core.models.FileDB;
import com.nikitalipatov.handmadeshop.core.models.Product;
import com.nikitalipatov.handmadeshop.core.repositories.CategoryRepository;
import com.nikitalipatov.handmadeshop.core.repositories.ProductRepository;
import com.nikitalipatov.handmadeshop.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository, FileStorageService fileStorageService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    public Set<Category> getCategoriesSet() {
        return new HashSet<>(categoryRepository.findAll());
    }

    public Category addNewCategory(CategoryDTO categoryDTO) throws IOException {
        Category newCategory = new Category();
        newCategory.setId(UUID.randomUUID());
        newCategory.setName(categoryDTO.getName());
        FileDB image = fileStorageService.store(categoryDTO.getImage());
        newCategory.setImage(image.getId());
        Set<Category> categories = new HashSet<>();
        categories.add(newCategory);
        return categoryRepository.save(newCategory);
    }

    public Optional<Boolean> deleteCategory(UUID id) {
        Optional<Category> result = categoryRepository.findById(id);
        List<Product> products = productRepository.findAllByCategory(result.get().getName());
        for (Product product : products) {
            product.setCategory("Категория не указана");
            productRepository.save(product);
        }
        return result
                .map(catalog -> {
                    categoryRepository.deleteById(id);
                    return true;
                });
    }

    public Optional<Category> editCategory(UUID id, CategoryDTO categoryDTO) {
        Optional<Category> result = categoryRepository.findById(id);
        if (categoryDTO.getName() !=null || categoryDTO.getName().isEmpty()) {
            List<Product> products = productRepository.findAllByCategory(result.get().getName());
            for (Product product : products) {
                product.setCategory(categoryDTO.getName());
                productRepository.save(product);
            }
        }
        return result
                .map(e -> {
                    e.setName(categoryDTO.getName());
                   if(categoryDTO.getImage() != null) {
                       try {
                            FileDB image = fileStorageService.store(categoryDTO.getImage());
                            e.setImage(image.getId());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                   }
                    return categoryRepository.save(e);
                });
    }
}
