package com.example.statistic.listener;

import com.example.statistic.domain.repository.StatisticRepository;
import com.example.statistic.mapper.StatisticMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablito.common.kafka.ProductKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasketProductListener {

    private final ObjectMapper objectMapper;

    private final StatisticMapper statisticMapper;

    private final StatisticRepository statisticRepository;

    @KafkaListener(topics = "statistic")
    public void listener(@Payload Message<?> message) throws JsonProcessingException {
        Object payload = message.getPayload();
        ProductKafka productKafka = objectMapper.readValue(payload.toString(), ProductKafka.class);
        statisticRepository.save(statisticMapper.toStatistic(productKafka));
    }
}
