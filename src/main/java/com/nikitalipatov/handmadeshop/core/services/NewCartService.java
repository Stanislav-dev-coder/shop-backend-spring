package com.nikitalipatov.handmadeshop.core.services;

import com.nikitalipatov.handmadeshop.core.models.NewCart;
import com.nikitalipatov.handmadeshop.core.models.Product;
import com.nikitalipatov.handmadeshop.core.repositories.NewCartRepository;
import com.nikitalipatov.handmadeshop.helpers.CartSummary;
import com.nikitalipatov.handmadeshop.dto.NewCartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NewCartService {

    private final UserService userService;
    private final ProductService productService;
    private final NewCartRepository newCartRepository;

    @Autowired
    public NewCartService(UserService userService, ProductService productService, NewCartRepository newCartRepository) {
        this.userService = userService;
        this.productService = productService;
        this.newCartRepository = newCartRepository;
    }

    public NewCart addProductToCart(NewCartDTO newCartDTO, HttpServletRequest request) {
        NewCart newCart = new NewCart();
        newCart.setProductId(newCartDTO.getProductId());
        newCart.setUserId(userService.findUser(request).get().getId());
        newCart.setProduct(productService.getById(newCartDTO.getProductId()).get());
        newCart.setAmount(newCartDTO.getAmount());
        return newCartRepository.save(newCart);
    }

    public List<NewCart> getUserCart(HttpServletRequest request) {
        return newCartRepository.findAllByUserId(userService.findUser(request).get().getId());
    }

    public Optional<Boolean> deleteProductFromCart(UUID productId, HttpServletRequest request) {
        Optional<NewCart> result = newCartRepository.findByProductIdAndUserId(productId,
                userService.findUser(request).get().getId());
        return result
                .map(e -> {
                    newCartRepository.deleteByProductIdAndUserId(productId,
                            userService.findUser(request).get().getId());
                    return true;
                });
    }

    public Optional<NewCart> modifyProductAmountInCart(NewCartDTO newCartDTO, HttpServletRequest request) {
        Optional<NewCart> result = newCartRepository.findByProductIdAndUserId(newCartDTO.getProductId(),
                userService.findUser(request).get().getId());
        return result
                .map(e -> {
                    if (result.get().getProduct().getAmount() < newCartDTO.getAmount()) {
                        e.setAmount(result.get().getProduct().getAmount());
                    } else if (newCartDTO.getAmount() < 1) {
                        e.setAmount(1);
                    } else {
                        e.setAmount(newCartDTO.getAmount());
                    }
                    return newCartRepository.save(e);
                });
    }

    public List<Object> cartSummary(HttpServletRequest request) {
        List<NewCart> result= newCartRepository.findAllByUserId(userService.findUser(request).get().getId());
        List<Product> products = new ArrayList<>();
        List<Integer> amounts = new ArrayList<>();
        for (NewCart newCart : result) {
            products.add(newCart.getProduct());
            amounts.add(newCart.getAmount());
        }
        CartSummary cartSummary = new CartSummary();
        return cartSummary.cartSummary(products, amounts);
    }

    public List<Object> cartSummaryForOrders(List<UUID> productsUUID, HttpServletRequest request) {
        List<NewCart> result= newCartRepository.findAllByUserIdAndProductIdIn(userService.findUser(request).get().getId(), productsUUID);
        List<Product> products = new ArrayList<>();
        List<Integer> amounts = new ArrayList<>();
        for (NewCart newCart : result) {
            products.add(newCart.getProduct());
            amounts.add(newCart.getAmount());
        }
        CartSummary cartSummary = new CartSummary();
        return cartSummary.cartSummary(products, amounts);
    }

    public List<NewCart> findUserCart(List<UUID> products, HttpServletRequest request) {
            return newCartRepository.findAllByUserIdAndProductIdIn(userService.findUser(request).get().getId(), products);
    }

    public void deleteAllUserCart(HttpServletRequest request) {
        newCartRepository.deleteAllByUserId(userService.findUser(request).get().getId());
    }

    public void deleteSelectedCartItems(HttpServletRequest request, ArrayList<String> list){
        for (int i = 0; i < list.size(); i++) {
            newCartRepository.deleteByProductIdAndUserId(UUID.fromString(list.get(i)), userService.findUser(request).get().getId());
        }
    }

    public List<UUID> findUserProducts(HttpServletRequest request) {
        List<NewCart> cart = newCartRepository.findAllByUserId(userService.findUser(request).get().getId());
        List<UUID> products = new ArrayList<>();
        for (NewCart newCart : cart) {
            products.add(newCart.getProductId());
        }
        return products;
    }

    public void deleteProductFromUserCart(UUID productId) {
        newCartRepository.deleteAllByProductId(productId);
    }
}

