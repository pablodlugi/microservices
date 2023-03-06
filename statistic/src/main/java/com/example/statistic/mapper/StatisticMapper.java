package com.example.statistic.mapper;

import com.example.statistic.domain.dao.Statistic;
import com.pablito.common.kafka.ProductKafka;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    @Mapping(source = "id", target = "productId")
    @Mapping(source = "name", target = "productName")
    @Mapping(source = "code", target = "productCode")
    @Mapping(source = "price", target = "productPrice")
    @Mapping(source = "quantity", target = "productQuantity")
    @Mapping(target = "id", ignore = true)
    Statistic toStatistic(ProductKafka productKafka);
}
