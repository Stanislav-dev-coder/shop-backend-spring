package com.nikitalipatov.handmadeshop.controllers;

import com.nikitalipatov.handmadeshop.core.models.Category;
import com.nikitalipatov.handmadeshop.core.services.CategoryService;
import com.nikitalipatov.handmadeshop.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Transactional
@RequestMapping(value = "/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get")
    public ResponseEntity<Set<Category>> getCategoriesSet() {
        Set<Category> result = categoryService.getCategoriesSet();
        return ResponseEntity.ok(result);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/add")
    public ResponseEntity<Category> addNewCategory(@RequestParam("name") String name,
                                                   @RequestParam("image")MultipartFile image) throws IOException {
        CategoryDTO categoryDTO = new CategoryDTO(name, image);
        Category result = categoryService.addNewCategory(categoryDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable(name = "id") UUID id) {
        Optional<Boolean> result = categoryService.deleteCategory(id);
        return result
                .map(e -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/modify/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Category> editCategory(@PathVariable(name = "id")UUID id,
                                                 @RequestParam(required = false, name = "name") String name,
                                                 @RequestParam(required = false, name = "image")MultipartFile image) {
        CategoryDTO categoryDTO = new CategoryDTO(name, image);
        Optional<Category> result = categoryService.editCategory(id, categoryDTO);
        return result
                .map(e -> ResponseEntity.ok(e))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
