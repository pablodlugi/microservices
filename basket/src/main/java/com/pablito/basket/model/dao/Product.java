package com.pablito.basket.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {

    private Long id;

    private String name;

    private String code;

    private Double price;

    private Long quantity;

    private String imagePath;
}
