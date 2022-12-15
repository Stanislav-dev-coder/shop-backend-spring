package com.nikitalipatov.handmadeshop.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDTO {
    private boolean sale;
    private String[] categories;
    private int priceFrom = 1;
    private int priceTo = Integer.MAX_VALUE;
    private int pageNumber = 0;
    private int pageSize = 4;
    private String sortBy = "price";
    private String sortDirection = "ASC";
    private String searchText;

    public ProductFilterDTO(boolean sale) {
        this.sale = sale;
    }
}
