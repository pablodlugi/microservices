package com.example.statistic;

import com.example.statistic.domain.dao.Statistic;
import com.example.statistic.domain.repository.StatisticRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.pablito.common.kafka.ProductKafka;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class StatisticKafkaTest {

    private static final WireMockServer SCHEMA_REGISTRY_SERVER = new WireMockServer(8081);

//    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch")
                    .withTag("7.17.0"));


    static {
        ELASTICSEARCH_CONTAINER.addExposedPorts(9200, 9300);
        ELASTICSEARCH_CONTAINER.setWaitStrategy(Wait.forHttp("/")
                .forPort(9200)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofSeconds(300)));
        ELASTICSEARCH_CONTAINER.start();

//        KAFKA_CONTAINER.withEnv("KAFKA_CREATE_TOPICS", "statistic");
//        KAFKA_CONTAINER.start();
    }

    @DynamicPropertySource //uzupełnia zmienne środowiskowe w trakcie działania aplikacji
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.elasticsearch.rest.uris",
                () -> "http://localhost:" + ELASTICSEARCH_CONTAINER.getMappedPort(9200));

//        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers",
//                KAFKA_CONTAINER::getBootstrapServers);
    }

    @BeforeAll
    static void beforeAll() {
        SCHEMA_REGISTRY_SERVER.start();
    }

    @AfterAll
    static void afterAll() {
        SCHEMA_REGISTRY_SERVER.stop();
    }

    @Autowired
    private KafkaTemplate<String, ProductKafka> kafkaTemplate;

    @Autowired
    private StatisticRepository statisticRepository;

    @Test
    void shouldSaveStatisticFromKafka() {

        SCHEMA_REGISTRY_SERVER.stubFor(post(urlEqualTo("/subjects/statistic-value/versions?normalize=false"))
                .willReturn(okJson("{}")));

        kafkaTemplate.send("statistic", ProductKafka.newBuilder()
                .setId(1L)
                .setCode("ABCD")
                .setName("KAPCIUSZKI")
                .setPrice(25.00)
                .setQuantity(2L)
                .build());

        System.out.println("KAFKA SEND \n");

        await().atMost(Duration.ofSeconds(10)).until(() -> {
            List<Statistic> statisticList = statisticRepository.findByProductId(1L);
            return statisticList.size() == 1 && statisticList.get(0).getProductCode().equals("ABCD")
                    && statisticList.get(0).getProductName().equals("KAPCIUSZKI")
                    && statisticList.get(0).getProductPrice().equals(25.00)
                    && statisticList.get(0).getProductQuantity().equals(2L)
                    && statisticList.get(0).getProductId().equals(1L);
        });
    }
}
