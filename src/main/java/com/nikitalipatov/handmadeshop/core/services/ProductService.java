package com.nikitalipatov.handmadeshop.core.services;

import com.nikitalipatov.handmadeshop.core.models.*;
import com.nikitalipatov.handmadeshop.core.repositories.CommentsRepository;
import com.nikitalipatov.handmadeshop.core.repositories.NewCartRepository;
import com.nikitalipatov.handmadeshop.core.repositories.ProductRepository;
import com.nikitalipatov.handmadeshop.dto.ProductEditingDTO;
import com.nikitalipatov.handmadeshop.dto.ProductCreationDTO;
import com.nikitalipatov.handmadeshop.dto.ProductFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.nikitalipatov.handmadeshop.specifications.ProductSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final NewCartRepository newCartRepository;
    private final FileStorageService fileStorageService;
    private final CategoryService categoryService;
    private final CommentsRepository commentsRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          NewCartRepository newCartRepository,
                          CategoryService categoryService,
                          FileStorageService fileStorageService,
                          CommentsRepository commentsRepository) {
        this.productRepository = productRepository;
        this.newCartRepository = newCartRepository;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
        this.commentsRepository = commentsRepository;
    }

    public Product save(ProductCreationDTO productCreationDTO) throws IOException {
        Product newEntity = new Product();
        newEntity.setId(UUID.randomUUID());
        newEntity.setDescription(productCreationDTO.getDescription());
        newEntity.setAmount(productCreationDTO.getAmount());
        newEntity.setName(productCreationDTO.getName());
        FileDB image = fileStorageService.store(productCreationDTO.getImage());
        newEntity.setImage(image.getId());
        newEntity.setPrice(productCreationDTO.getPrice());
        newEntity.setRating(0.0);
        newEntity.setCategory(productCreationDTO.getCategory());
        newEntity.setSale("Товар не участвует в акции");
        return productRepository.save(newEntity);
    }

    public Page<Product> listAll(Pageable pageable) {
        Page<Product> result = productRepository.findAll(pageable);
        return result;
    }

    public Page<Product> findAllWithFilters(ProductFilterDTO productFilterDTO) {
        Specification<Product> specification = where(isDeal(productFilterDTO.isSale()).and(inCategories(productFilterDTO.getCategories())))
                        .and(inPriceRange(productFilterDTO.getPriceFrom(), productFilterDTO.getPriceTo()))
                        .and(likeSearchText(productFilterDTO.getSearchText()));
        Pageable pageable = PageRequest.of(
                productFilterDTO.getPageNumber(),
                productFilterDTO.getPageSize(),
                Sort.by(Sort.Direction.valueOf(productFilterDTO.getSortDirection()), productFilterDTO.getSortBy()));
        Page<Product> result = productRepository.findAll(specification, pageable);
        return result;
    }

    public List<Product> getPopularProducts() {
        return productRepository.findTop4ByRatingGreaterThanEqual(4.0);
//        for (int i = 0; i < products.size() - 2; i++) {
//            products.remove(i);
//        }
//        return pr.subList(pr.size() - 4, pr.size());
    }

    public List<Product> getNewProducts() {
        List<Product> pr = productRepository.findAll();
        pr.subList(pr.size() - 4, pr.size());
        List<Product> products = productRepository.findLast4ByRatingGreaterThanEqual(0.0);
        for (int i = 0; i < products.size() - 2; i++) {
            products.remove(i);
        }
        return pr.subList(pr.size() - 4, pr.size());
    }

    public Optional<Product> getById(UUID id) {
        return productRepository.findById(id);
    }



    public Optional<Product> editCatalog(UUID uuid, ProductCreationDTO productCreationDTO) {
        Optional<Product> result = productRepository.findById(uuid);
        double salePercent = (result.get().getPrice() - result.get().getDiscountPrice()) * 100 / result.get().getPrice();
        return result
                .map(entity -> {
                    entity.setName(productCreationDTO.getName());
                    entity.setDescription(productCreationDTO.getDescription());
                    entity.setPrice(productCreationDTO.getPrice());
                    entity.setAmount(productCreationDTO.getAmount());
                    entity.setCategory(productCreationDTO.getCategory());
                    if (result.get().getSale().equals("Товар не участвует в акции")) {
                        entity.setDiscountPrice(0.0);
                    } else {
                        entity.setDiscountPrice(productCreationDTO.getPrice() - (productCreationDTO.getPrice() / 100 * salePercent));
                    }
                    if(productCreationDTO.getImage() != null) {
                        try {
                            FileDB image = fileStorageService.store(productCreationDTO.getImage());
                            entity.setImage(image.getId());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    return productRepository.save(entity);
                });

    }

    public Optional<Boolean> deleteById(UUID id) {
        newCartRepository.deleteAllByProductId(id);
        Optional<Product> result = productRepository.findById(id);
        return result
                .map(catalog -> {
                    productRepository.deleteById(id);
                    return true;
                });
    }

    public void disableSales(String sale) {
        List<Product> products = productRepository.findAllBySale(sale);
        for (Product product : products) {
            product.setDiscountPrice(0.0);
            product.setSale("Товар не участвует в акции");
        }
    }

    public void disableSaleManually(String sale) {
        List<Product> products = productRepository.findAllBySale(sale);
        for (Product product : products) {
            product.setSale("Товар не участвует в акции");
            product.setDiscountPrice(0.0);
        }
    }

    public void modifyProductAmount(List<NewCart> userCart) {
        for (int i = 0; i < userCart.size(); i++) {
            Optional<Product> result = productRepository.findById(userCart.get(i).getProductId());
            int newAmount = result.get().getAmount() - userCart.get(i).getAmount();
            result.get().setAmount(newAmount);
            productRepository.save(result.get());
        }
    }

    public void calculateRating(UUID id) {
        List<Comments> comments = commentsRepository.findAllByProductId(id);
        double commentsKol = comments.size();
        double commentsTotalRating = 0;
        for (Comments comment : comments) {
            commentsTotalRating = commentsTotalRating + comment.getRating();
        }
        Optional<Product> product = productRepository.findById(id);
        product.get().setRating(commentsTotalRating / commentsKol);
        productRepository.save(product.get());
    }
}
