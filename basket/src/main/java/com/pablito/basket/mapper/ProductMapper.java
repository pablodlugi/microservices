package com.pablito.basket.mapper;

import com.pablito.basket.model.dao.Product;
import com.pablito.common.dto.ProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    List<ProductDto> toDtoList(List<Product> products);

}
