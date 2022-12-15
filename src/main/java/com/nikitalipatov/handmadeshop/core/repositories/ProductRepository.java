package com.nikitalipatov.handmadeshop.core.repositories;

import com.nikitalipatov.handmadeshop.core.models.Product;
import com.nikitalipatov.handmadeshop.core.models.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor {
    List<Product> findAllByCategory(String category);
    List<Product> findAllByNameIsLike(String name);
    List<Product> findAllByIdIn(List<UUID> productIds);
    List<Product> findAllBySale(String sale);
    List<Product> findTop4ByRatingGreaterThanEqual(double rating);
    List<Product> findLast4ByRatingGreaterThanEqual(double rating);
    <Optional>Product findByName(String name);
}
