package com.pablito.basket.service;

import com.pablito.basket.model.dao.Product;

import java.util.List;

public interface BasketService {

    void addProduct(Long productId, Long quantity, String token);

    void deleteProduct(Long id, String token);

    void clearBasket(String token);

    List<Product> getAllProducts(String token);
}
