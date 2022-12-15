package com.nikitalipatov.handmadeshop.controllers;

import com.nikitalipatov.handmadeshop.core.models.Sale;
import com.nikitalipatov.handmadeshop.core.services.SaleService;
import com.nikitalipatov.handmadeshop.dto.SaleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Transactional
@RequestMapping(value = "/api/sale")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Sale> createSale(@RequestParam(name = "name") String name,
                                           @RequestParam(name = "image") MultipartFile image,
                                           @RequestParam(name = "date") String date,
                                           @RequestParam(name = "expirationDate") String expirationDate,
                                           @RequestParam(name = "discount") double discount,
                                           @RequestParam(name = "products") String[] products) {
       SaleDTO saleDTO = new SaleDTO(name, image, date,expirationDate,discount, products);
       Sale sale = saleService.createSale(saleDTO);
       return ResponseEntity.ok(sale);
    }

    @GetMapping("/get")
    public ResponseEntity<Page<Sale>> getSales() {
        Page<Sale> result = saleService.getSales();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
        public ResponseEntity<?> deleteSale(@PathVariable(name = "id")UUID saleId) {
        Optional<Boolean> result = saleService.deleteSaleManually(saleId);
        return result
                .map(e -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/modify/{id}")
    public ResponseEntity<Sale> modifySale(@PathVariable(name = "id") UUID id,
                                           @RequestParam(name = "name") String name,
                                           @RequestParam(required = false, name = "image") MultipartFile image,
                                           @RequestParam(name = "date") String date,
                                           @RequestParam(name = "expirationDate") String expirationDate,
                                           @RequestParam(name = "discount") double discount,
                                           @RequestParam(required = false, name = "products") String[] products) {
        SaleDTO saleDTO = new SaleDTO(name, image, date, expirationDate, discount,products);
        Optional<Sale> result = saleService.modifySale(id, saleDTO);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
