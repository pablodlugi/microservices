package com.pablito.basket.client;

import com.pablito.basket.exception.ProductNotFoundException;
import com.pablito.common.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClientFacade {

    private final ProductClient productClient;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public ProductDto getProductById(Long id) {
        log.info("TRY TO GET PRODUCT");
        return productClient.getProductById(id);
    }

    @Recover
    public ProductDto recovery(Exception e, Long id) {
        log.warn("Could not get product response: ", e);
        throw new ProductNotFoundException(e.getMessage());
    }

}
