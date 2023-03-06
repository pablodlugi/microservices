package com.pablito.basket.service;

import com.pablito.basket.client.ProductClient;
import com.pablito.basket.client.ProductClientFacade;
import com.pablito.basket.client.UserClient;
import com.pablito.basket.model.dao.Basket;
import com.pablito.basket.model.dao.Product;
import com.pablito.basket.repository.BasketRepository;
import com.pablito.common.dto.ProductDto;
import com.pablito.common.dto.UserDto;
import com.pablito.common.kafka.ProductKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;

//    private final ProductClient productClient;

    private final ProductClientFacade productClientFacade;

    private final UserClient userClient;


    private final KafkaTemplate<String, ProductKafka> kafkaTemplate;

    @Override
    public void addProduct(Long productId, Long quantity, String token) {
        UserDto currentUser = userClient.getCurrentUser(token);
        ProductDto productToBasket = productClientFacade.getProductById(productId);
        Basket basket = basketRepository.findByUserId(currentUser.getId()).orElseGet(() -> Basket.builder()
                .userId(currentUser.getId())
                .product(Product.builder()
                        .id(productToBasket.getId())
                        .name(productToBasket.getName())
                        .quantity(quantity)
                        .price(productToBasket.getPrice())
                        .code(productToBasket.getCode())
                        .imagePath(productToBasket.getImagePath())
                        .build())
                .build());

        Optional<Product> optionalProduct = basket.getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst();

        optionalProduct.ifPresentOrElse(product -> {
                    if (productToBasket.getQuantity() > quantity) {
                        product.setQuantity(quantity);
                    } else {
                        product.setQuantity(productToBasket.getQuantity());
                    }
                }, () -> basket.getProducts().add(Product.builder()
                        .id(productToBasket.getId())
                        .name(productToBasket.getName())
                        .quantity(productToBasket.getQuantity() > quantity ? quantity : productToBasket.getQuantity())
                        .price(productToBasket.getPrice())
                        .code(productToBasket.getCode())
                        .imagePath(productToBasket.getImagePath())
                        .build())
        );

        basketRepository.save(basket);
        kafkaTemplate.send("statistic", ProductKafka.newBuilder()
                .setId(productToBasket.getId())
                .setName(productToBasket.getName())
                .setPrice(productToBasket.getPrice())
                .setQuantity(productToBasket.getQuantity() > quantity ? quantity : productToBasket.getQuantity())
                .setCode(productToBasket.getCode())
                .build());
    }

    @Override
    public void deleteProduct(Long productId, String token) {
        processUserBasket(token, basket -> {
            basket.getProducts()
                    .removeIf(product -> product.getId().equals(productId));
            basketRepository.save(basket);
        });
    }

    @Override
    public void clearBasket(String token) {
        processUserBasket(token, basket -> {
            basket.getProducts().clear();
            basketRepository.save(basket);
        });
    }

    @Override
    public List<Product> getAllProducts(String token) {
        UserDto userDto = userClient.getCurrentUser(token);
        return basketRepository.findByUserId(userDto.getId()).map(Basket::getProducts).orElse(Collections.emptyList());
    }

    private void processUserBasket(String token, Consumer<Basket> consumer) {
        UserDto userDto = userClient.getCurrentUser(token);
        basketRepository.findByUserId(userDto.getId()).ifPresent(consumer);
    }

}
