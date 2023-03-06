package com.pablito.basket.controller;

import com.pablito.basket.client.ProductClient;
import com.pablito.basket.mapper.ProductMapper;
import com.pablito.basket.model.dto.BasketDto;
import com.pablito.basket.service.BasketService;
import com.pablito.common.dto.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
public class BasketController {

    private final ProductClient productClient;

    private final BasketService basketService;

    private final ProductMapper productMapper;

    @GetMapping("{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productClient.getProductById(id);
    }

    @PatchMapping
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void addProductToBasket(@RequestBody BasketDto basketDto, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        basketService.addProduct(basketDto.getProductId(), basketDto.getQuantity(), token);
    }

    @DeleteMapping("{productId}")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void deleteFromBasket(@PathVariable Long productId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        basketService.deleteProduct(productId, token);
    }

    @DeleteMapping
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void clearBasket(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        basketService.clearBasket(token);
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public List<ProductDto> getProductList(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return productMapper.toDtoList(basketService.getAllProducts(token));
    }
//    private final RestTemplate restTemplate;
//
//    @GetMapping("{id}")
//    public Map test(@PathVariable Long id){
//        return restTemplate.getForObject("http://product-service/api/v1/products/" + id, Map.class);
//    }
}
