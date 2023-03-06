package com.example.statistic.domain.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "statistic")
public class Statistic {

    @Id
    private String id;

    private Long productId;

    private String productName;

    private String productCode;

    private Double productPrice;

    private Long productQuantity;

}
