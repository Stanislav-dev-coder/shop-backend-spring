package com.nikitalipatov.handmadeshop.core.repositories;

import com.nikitalipatov.handmadeshop.core.models.NewOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewOrderRepository extends JpaRepository<NewOrder, UUID> {
    List<NewOrder> findAllByUserId(Long userId);
    Optional<NewOrder> findByOrderId(UUID orderId);
    Optional<NewOrder> deleteByOrderIdAndUserId(UUID orderId, Long userId);
}
