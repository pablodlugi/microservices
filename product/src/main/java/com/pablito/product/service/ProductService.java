package com.pablito.product.service;

import com.pablito.product.domain.dao.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    @CachePut(cacheNames = "product", key = "#result.id")
        //spel
    Product save(Product product, MultipartFile image);

    @CachePut(cacheNames = "product", key = "#result.id")
    Product update(Product product, Long id, MultipartFile image);

    @CacheEvict(cacheNames = "product", key = "#id")
    void delete(Long id);

    @Cacheable(cacheNames = "product", key = "#id")
    Product getProductById(Long id);

    Page<Product> getPage(Pageable pageable);


}
