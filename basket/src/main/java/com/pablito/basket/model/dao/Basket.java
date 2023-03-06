package com.pablito.basket.model.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(indexName = "basket")
public class Basket {

    @Id
    private String id;

    private Long userId;

    @Singular
    private List<Product> products;
}
