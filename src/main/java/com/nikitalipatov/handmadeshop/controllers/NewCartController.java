package com.nikitalipatov.handmadeshop.controllers;

import com.nikitalipatov.handmadeshop.core.models.NewCart;
import com.nikitalipatov.handmadeshop.core.services.NewCartService;
import com.nikitalipatov.handmadeshop.dto.NewCartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Transactional
@RequestMapping(value = "/api/cart")
public class NewCartController {

    private final NewCartService newCartService;

    @Autowired
    public NewCartController(NewCartService newCartService) {
        this.newCartService = newCartService;
    }

    @GetMapping("/summary")
    public ResponseEntity<List<Object>> cartSummary(HttpServletRequest request) {
        List<Object> result = newCartService.cartSummary(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<NewCart> addProductToCart(@RequestBody NewCartDTO newCartDTO, HttpServletRequest request) {
        NewCart result = newCartService.addProductToCart(newCartDTO, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get")
    public ResponseEntity<Page<NewCart>> getUserCart(HttpServletRequest request) {
        Page<NewCart> result = newCartService.getUserCart(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products")
    public ResponseEntity<List<UUID>> getUserProducts(HttpServletRequest request) {
        List<UUID> result = newCartService.findUserProducts(request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable(name = "id")UUID productId, HttpServletRequest request) {
        Optional<Boolean> result = newCartService.deleteProductFromCart(productId, request);
        return result
                .map(e -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/modify")
    public ResponseEntity<NewCart> modifyProductAmountInCart(@RequestBody NewCartDTO newCartDTO, HttpServletRequest request) {
        Optional<NewCart> result = newCartService.modifyProductAmountInCart(newCartDTO, request);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAll(HttpServletRequest request){
        newCartService.deleteAllUserCart(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/selected")
    public  ResponseEntity<?> deleteSelectedCartItems(HttpServletRequest request, @RequestBody ArrayList<String> list){
        newCartService.deleteSelectedCartItems(request, list);
        return  ResponseEntity.noContent().build();
    }
}
