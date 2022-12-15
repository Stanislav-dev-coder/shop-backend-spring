package com.nikitalipatov.handmadeshop.specifications;

import com.nikitalipatov.handmadeshop.core.models.Product;
import com.nikitalipatov.handmadeshop.core.models.Product_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * Specifications JPA Class for advanced queries for Product entity
 * @see com.nikitalipatov.handmadeshop.controllers.ProductController
 */

@Component
public class ProductSpecifications {

    /**
     * This method allows to set <b>deal</b> as search parameter
     * @param isDeal status of deal: is it exist or not
     */
    public static Specification<Product> isDeal(boolean isDeal) {
            return ((root, criteriaQuery, criteriaBuilder) -> {
                if (!isDeal) {
                    return criteriaBuilder.conjunction();
                } else return criteriaBuilder.greaterThan(root.get(Product_.DISCOUNT_PRICE), 0.0);
            });
    }

    /**
     * This method allows to set <b>price</b> as search parameter
     * @param priceFrom starting price of a product
     * @param priceTo the final price of a product
     */
    public static Specification<Product> inPriceRange(int priceFrom, int priceTo) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), priceFrom, priceTo));
    }

    /**
     * This method allows to set <b>categories</b> as search parameter
     * @param categories array of available categories of products
     */
    public static Specification<Product> inCategories(String[] categories) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (categories == null | (categories != null ? categories.length : 0) == 0) {
                return criteriaBuilder.conjunction();
            }
            return root.get(Product_.CATEGORY).in(Arrays.asList(categories));
        });
    }

    /**
     * This method allows to set <b>name of products</b> as search parameter
     * @param searchText a piece of product name
     */
    public static Specification<Product> likeSearchText(String searchText) {
        return ((root, query, criteriaBuilder) -> {
            if (searchText == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(Product_.NAME), "%" + searchText + "%");
        });
    }
}
