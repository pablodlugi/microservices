package com.example.statistic.domain.repository;

import com.example.statistic.domain.dao.Statistic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface StatisticRepository extends ElasticsearchRepository<Statistic, String> {

    List<Statistic> findByProductId(Long productId);
}
