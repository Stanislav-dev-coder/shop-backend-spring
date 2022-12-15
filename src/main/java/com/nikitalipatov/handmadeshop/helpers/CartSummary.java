package com.nikitalipatov.handmadeshop.helpers;

import com.nikitalipatov.handmadeshop.core.models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper Class to calculate user cart data such as <b>number of products in cart, cart summary price and discount</b>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartSummary {
    private int numberOfProductsInCart;
    private double priceWithoutDiscount;
    private double discount;
    private double priceWithDiscount;
    private List<Object> cartSummaryList;

    public List<Object> cartSummary(List<Product> products, List<Integer> amounts) {
        int amountValue = 0;
        double priceValue = 0.0;
        for (int i=0; i<products.size(); i++) {
            amountValue = amountValue + amounts.get(i);
            priceValue = priceValue + (products.get(i).getPrice() * amounts.get(i));
        }
        setNumberOfProductsInCart(amountValue);
        setPriceWithoutDiscount(priceValue);
        if (priceWithoutDiscount >= 3000 && priceWithoutDiscount <= 5000) {
            setDiscount(getPriceWithoutDiscount() * 0.03);
        }
        if (priceWithoutDiscount >= 5000 && priceWithoutDiscount <= 10000) {
            setDiscount(getPriceWithoutDiscount() * 0.05);
        }
        if (priceWithoutDiscount >= 10000) {
            setDiscount(getPriceWithoutDiscount() * 0.1);
        }
        setPriceWithDiscount(getPriceWithoutDiscount() - getDiscount());
        cartSummaryList = new ArrayList<>();
        cartSummaryList.add(numberOfProductsInCart);
        cartSummaryList.add(priceWithoutDiscount);
        cartSummaryList.add(discount);
        cartSummaryList.add(priceWithDiscount);
        return cartSummaryList;
    }
}
