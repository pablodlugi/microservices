package com.pablito.basket.repository;

import com.pablito.basket.model.dao.Basket;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface BasketRepository extends ElasticsearchRepository<Basket, String> {

    Optional<Basket> findByUserId(Long userId);

}
