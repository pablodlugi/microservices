package com.pablito.product.mapper;

import com.pablito.product.domain.dao.Product;
import com.pablito.product.domain.dto.ProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper extends AuditableMapper<Product, ProductDto> {
    Product toDao(ProductDto productDto);

    ProductDto toDto(Product product);

    List<ProductDto> toDtoList(List<Product> products);
}
