package com.nikitalipatov.handmadeshop.core.repositories;

import com.nikitalipatov.handmadeshop.core.models.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentsRepository extends JpaRepository<Comments, UUID> {
    Optional<Comments> findByProductIdAndUserId(UUID productId, Long userId);
    Optional<Comments> deleteByProductIdAndUserId(UUID productId, Long userId);
    Boolean existsByProductIdAndUserId(UUID productId, Long userId);
    List<Comments> findAllByProductId(UUID productId);

}
