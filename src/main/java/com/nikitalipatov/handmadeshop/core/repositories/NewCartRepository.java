package com.nikitalipatov.handmadeshop.core.repositories;

import com.nikitalipatov.handmadeshop.core.models.NewCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewCartRepository extends JpaRepository<NewCart, UUID> {
    List<NewCart> findAllByUserId(Long userId);
    Optional<NewCart> findByProductIdAndUserId(UUID productId, Long userId);
    List<NewCart> findAllByUserIdAndProductIdIn(Long userId, List<UUID> products);
    Optional<NewCart> deleteByProductIdAndUserId(UUID productId, Long userId);
    Optional<NewCart> deleteAllByUserId(Long userId);

    Optional<NewCart> deleteAllByProductId(UUID productId);
}
