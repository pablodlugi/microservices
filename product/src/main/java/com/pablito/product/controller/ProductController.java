package com.pablito.product.controller;

import com.pablito.product.domain.dto.ProductDto;
import com.pablito.product.mapper.ProductMapper;
import com.pablito.product.service.ProductService;
import com.pablito.product.validator.FileExtensionValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Validated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public ProductDto saveProduct(@RequestPart @Valid ProductDto product, @FileExtensionValid @Valid @RequestPart MultipartFile image) { //Valid wlacza walidacje z DTO NotBlank NOTNull
        return productMapper.toDto(productService.save(productMapper.toDao(product), image));
    }

    @GetMapping("{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        log.info("GET PRODUCT BY ID");
        return productMapper.toDto(productService.getProductById(id));
    }

    @Validated
    @PutMapping
    @PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public ProductDto updateProductById(@RequestPart @Valid ProductDto product, @RequestPart Long id, @FileExtensionValid @Valid @RequestPart MultipartFile image) {
        return productMapper.toDto(productService.update(productMapper.toDao(product), id, image));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void deleteProductById(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping
    public Page<ProductDto> getProductPage(@RequestParam int page, @RequestParam int size) {
        return productService.getPage(PageRequest.of(page, size)).map(productMapper::toDto);
    }
}
