package com.nikitalipatov.handmadeshop.controllers;

import com.nikitalipatov.handmadeshop.core.models.NewOrder;
import com.nikitalipatov.handmadeshop.core.models.OrderStatus;
import com.nikitalipatov.handmadeshop.core.services.NewOrderService;
import com.nikitalipatov.handmadeshop.dto.OrderDTO;
import com.nikitalipatov.handmadeshop.dto.OrderStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Transactional
@RequestMapping(value = "/api/order")
public class NewOrderController {

    private final NewOrderService newOrderService;

    @Autowired
    public NewOrderController(NewOrderService newOrderService) {
        this.newOrderService = newOrderService;
    }

    @GetMapping("/status")
    public ResponseEntity<List<OrderStatus>> getOrderStatus() {
        return ResponseEntity.ok(newOrderService.getOrderStatus());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Optional<NewOrder>> getOrder(@PathVariable(name = "id")UUID id) {
        return ResponseEntity.ok(newOrderService.getOrder(id));
    }

    @PostMapping("/add")
    public ResponseEntity<NewOrder> createOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        return ResponseEntity.ok(newOrderService.createOrder(orderDTO, request));
    }

    @GetMapping("/get")
    public ResponseEntity<List<NewOrder>> getOrders(HttpServletRequest request) {
        return ResponseEntity.ok(newOrderService.getOrders(request));
    }

    @PutMapping("/modify")
    public ResponseEntity<NewOrder> modifyOrderStatus(@RequestBody OrderStatusDTO orderStatusDTO) {
        Optional<NewOrder> result = newOrderService.modifyOrderStatus(orderStatusDTO);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
