package com.example.statistic.listener;

import com.example.statistic.domain.repository.StatisticRepository;
import com.example.statistic.mapper.StatisticMapper;
import com.pablito.common.kafka.ProductKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BasketProductListener {

    private final StatisticMapper statisticMapper;

    private final StatisticRepository statisticRepository;

    @KafkaListener(topics = "statistic", containerFactory = "productKafkaReader", groupId = "statistic-reader")
    public void listener(@Payload ProductKafka productKafka) {
        log.info("RECEIVED MESSAGE \n");
        statisticRepository.save(statisticMapper.toStatistic(productKafka));
    }
}
