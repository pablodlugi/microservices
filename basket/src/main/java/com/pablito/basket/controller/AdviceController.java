package com.pablito.basket.controller;

import com.pablito.basket.exception.ProductNotFoundException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public void handleProductNotFoundException(ProductNotFoundException e) {
        log.warn("Product not found in DB: ", e);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RequestNotPermitted.class)
    public void handleRequestNotPermittedException(RequestNotPermitted e) {
        log.warn("TOO MANY REQUESTS: ", e);
    }
}
