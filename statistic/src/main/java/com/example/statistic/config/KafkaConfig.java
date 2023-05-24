package com.example.statistic.config;

import com.pablito.common.kafka.ProductKafka;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean("productKafkaReader")
    public ConcurrentKafkaListenerContainerFactory<String, ProductKafka> concurrentKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl) {

        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put("schema.registry.url", schemaRegistryUrl);
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProperties.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, "5000");
        consumerProperties.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, "5000");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerProperties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        DefaultKafkaConsumerFactory<String, ProductKafka> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        ConcurrentKafkaListenerContainerFactory<String, ProductKafka> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(defaultKafkaConsumerFactory);
        factory.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());

        return factory;
    }
}
