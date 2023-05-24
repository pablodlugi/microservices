//package com.example.statistic.config;
//
//import com.pablito.common.kafka.ProductKafka;
//import lombok.RequiredArgsConstructor;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//@Configuration
//@RequiredArgsConstructor
//public class KafkaProducerConfig {
//
//    private final KafkaProperties kafkaProperties;
//
//    @Bean
//    public ProducerFactory<String, ProductKafka> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(), new StringSerializer(), new JsonSerializer<>());
//    }
//
//    @Bean
//    public KafkaTemplate<String, ProductKafka> kafkaTemplate(ProducerFactory<String, ProductKafka> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }
//
//
//}
