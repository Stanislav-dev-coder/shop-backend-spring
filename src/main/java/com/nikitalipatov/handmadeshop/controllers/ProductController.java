package com.nikitalipatov.handmadeshop.controllers;

import com.nikitalipatov.handmadeshop.core.models.Product;
import com.nikitalipatov.handmadeshop.core.repositories.ProductRepository;
import com.nikitalipatov.handmadeshop.core.services.ProductService;
import com.nikitalipatov.handmadeshop.dto.ProductCreationDTO;
import com.nikitalipatov.handmadeshop.dto.ProductFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Transactional
@RequestMapping(value = "/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<Product>> filterProducts(@RequestBody ProductFilterDTO productFilterDTO) {
        return ResponseEntity.ok(productService.findAllWithFilters(productFilterDTO));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> addItem(@RequestParam("name") String name,
                                           @RequestParam("description") String description,
                                           @RequestParam("amount") int amount,
                                           @RequestParam("image") MultipartFile image,
                                           @RequestParam("price") double price,
                                           @RequestParam("category") String category) throws IOException {
        ProductCreationDTO productCreationDTO = new ProductCreationDTO(name, description,  amount, image, price, category);
        Product resSave = productService.save(productCreationDTO);
        return ResponseEntity.ok(resSave);
    }

    @PutMapping(value = "/modify/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> editItem(@PathVariable(name = "id") UUID id,
                                            @RequestParam(name = "name") String name,
                                            @RequestParam(name = "description") String description,
                                            @RequestParam(name = "amount") int amount,
                                            @RequestParam(required = false, name = "image") MultipartFile image,
                                            @RequestParam(name = "price") double price,
                                            @RequestParam(name = "category") String category) {
        ProductCreationDTO productCreationDTO = new ProductCreationDTO(name, description, amount, image, price, category);
        Optional<Product> resEdit = productService.editCatalog(id, productCreationDTO);
        return resEdit
                .map(entity -> ResponseEntity.ok(entity))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteItem(@PathVariable(name = "id") UUID id) {
        Optional<Boolean> result = productService.deleteById(id);
        return result
                .map(e -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<Page<Product>> getCatalog(Pageable pageable) {
        Page<Product> catalogEntityList = productService.listAll(pageable);
        return ResponseEntity.ok(catalogEntityList);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopularProducts() {
        List<Product> popularProducts = productService.getPopularProducts();
        return ResponseEntity.ok(popularProducts);
    }

    @GetMapping("/new")
    public ResponseEntity<List<Product>> getNewProducts() {
        List<Product> newProducts = productService.getNewProducts();
        return ResponseEntity.ok(newProducts);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Product> getItem(@PathVariable(name = "id") UUID id) {
        Optional<Product> result = productService.getById(id);
        return result
                .map(entity -> ResponseEntity.ok(entity))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
