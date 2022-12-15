package com.nikitalipatov.handmadeshop.controllers;

import com.nikitalipatov.handmadeshop.core.models.NewOrder;
import com.nikitalipatov.handmadeshop.core.services.NewOrderService;
import com.nikitalipatov.handmadeshop.dto.OrderDTO;
import com.nikitalipatov.handmadeshop.dto.OrderStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
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


    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable(name = "id") UUID id, HttpServletRequest request) {
        Optional<Boolean> result = newOrderService.cancelOrder(id, request);
        return result
                .map(e -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/confirm/{id}")
    public ResponseEntity<NewOrder> confirmReceipt(@PathVariable(name = "id") UUID id) {
        Optional<NewOrder> result = newOrderService.confirmReceipt(id);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<NewOrder> createOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        return ResponseEntity.ok(newOrderService.createOrder(orderDTO, request));
    }

    @GetMapping("/get")
    public ResponseEntity<List<NewOrder>> getOrders(HttpServletRequest request) {
        return ResponseEntity.ok(newOrderService.getOrders(request));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<NewOrder>> getAllUserOrders() {
        return ResponseEntity.ok(newOrderService.getAllUserOrders());
    }

    @PutMapping("/modify")
    public ResponseEntity<NewOrder> modifyOrderStatus(@RequestBody OrderStatusDTO orderStatusDTO) {
        Optional<NewOrder> result = newOrderService.modifyOrderStatus(orderStatusDTO);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
