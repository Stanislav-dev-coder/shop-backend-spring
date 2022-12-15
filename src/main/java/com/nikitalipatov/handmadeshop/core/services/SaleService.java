package com.nikitalipatov.handmadeshop.core.services;

import com.nikitalipatov.handmadeshop.core.models.FileDB;
import com.nikitalipatov.handmadeshop.core.models.Product;
import com.nikitalipatov.handmadeshop.core.models.Sale;
import com.nikitalipatov.handmadeshop.core.repositories.ProductRepository;
import com.nikitalipatov.handmadeshop.core.repositories.SaleRepository;
import com.nikitalipatov.handmadeshop.dto.SaleDTO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service class for manipulation with <B>deals</B>.
 * In addition to performing REST operations, allows to delete deal automatically by schedule
 */

@Service
@Transactional
@EnableScheduling
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository, ProductService productService, FileStorageService fileStorageService, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.fileStorageService = fileStorageService;
        this.productRepository = productRepository;
    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void deleteByScheduler() {
        List<Sale> sales = saleRepository.findAllByExpirationDateBefore(new Date());
        for(int i = 0; i < sales.size(); i++) {
            productService.disableSales(sales.get(i).getName());
        }
        saleRepository.deleteAllByExpirationDateBefore(new Date());
    }

    /**
     * Method creates a <b>sale</b> and set it to products
     * @see Product
     */
    @SneakyThrows
    public Sale createSale(SaleDTO saleDTO) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Sale newSale = new Sale();
        newSale.setId(UUID.randomUUID());
        newSale.setName(saleDTO.getName());
        FileDB image = fileStorageService.store(saleDTO.getImage());
        newSale.setImage(image.getId());
        newSale.setDate(LocalDateTime.parse(saleDTO.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        newSale.setExpirationDate(LocalDateTime.parse(saleDTO.getExpirationDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        newSale.setDiscount(saleDTO.getDiscount());
        String[] products = saleDTO.getProducts();
        for (String item : products) {
            Optional<Product> product = productService.getById(UUID.fromString(item));
            product.get().setSale(saleDTO.getName());
            product.get().setDiscountPrice(product.get().getPrice() - product.get().getPrice() / 100 * saleDTO.getDiscount());
            productRepository.save(product.get());
        }
        return saleRepository.save(newSale);
    }

    public Page<Sale> getSales() {
        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.ASC, "expirationDate"));
        return saleRepository.findAll(pageable);
    }

    public Optional<Boolean> deleteSaleManually(UUID saleId) {
        Optional<Sale> result = saleRepository.findById(saleId);
        return result
                .map(e -> {
                    productService.disableSaleManually(result.get().getName());
                    saleRepository.deleteById(saleId);
                    return true;
                });
    }

    /**
     * Main purpose of this method to edit <b>sale</b> and all it products
     */
    public Optional<Sale> modifySale(UUID id, SaleDTO saleDTO) {
        Optional<Sale> result = saleRepository.findById(id);
        productService.disableSaleManually(result.get().getName());
        return result
                .map(e -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    e.setName(saleDTO.getName());
                    e.setDate(LocalDateTime.parse(saleDTO.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    e.setExpirationDate(LocalDateTime.parse(saleDTO.getExpirationDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    e.setDiscount(saleDTO.getDiscount());
                    if(saleDTO.getImage() != null) {
                        try {
                            FileDB image = fileStorageService.store(saleDTO.getImage());
                            e.setImage(image.getId());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    String[] products = saleDTO.getProducts();
                    for (String item : products) {
                        Optional<Product> product = productService.getById(UUID.fromString(item));
                        product.get().setSale(saleDTO.getName());
                        product.get().setDiscountPrice(product.get().getPrice() - product.get().getPrice() / 100 * saleDTO.getDiscount());
                        productRepository.save(product.get());
                    }
                    return saleRepository.save(e);
                });
    }
}
