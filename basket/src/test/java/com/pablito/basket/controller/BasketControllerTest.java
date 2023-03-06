package com.pablito.basket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.pablito.basket.controller.fixtures.TokenFixtures;
import com.pablito.basket.model.dao.Basket;
import com.pablito.basket.model.dao.Product;
import com.pablito.basket.model.dto.BasketDto;
import com.pablito.basket.repository.BasketRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class BasketControllerTest {

    private static final WireMockServer PRODUCT_SERVER = new WireMockServer(8082);

    private static final WireMockServer USER_SERVER = new WireMockServer(8080);

    private static final WireMockServer SCHEMA_REGISTRY_SERVER = new WireMockServer(8081);

    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

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

        KAFKA_CONTAINER.start();
    }

    @DynamicPropertySource //uzupełnia zmienne środowiskowe w trakcie działania aplikacji
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.elasticsearch.rest.uris",
                () -> "http://localhost:" + ELASTICSEARCH_CONTAINER.getMappedPort(9200));

        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers",
                KAFKA_CONTAINER::getBootstrapServers);

    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TokenFixtures tokenTestHelperClass;

    @Autowired
    private BasketRepository basketRepository;


    @BeforeAll
    static void beforeAll() {
        PRODUCT_SERVER.start();
        USER_SERVER.start();
        SCHEMA_REGISTRY_SERVER.start();
    }

    @AfterAll
    static void afterAll() {
        PRODUCT_SERVER.stop();
        USER_SERVER.stop();
        SCHEMA_REGISTRY_SERVER.stop();
    }

    @Test
    @WithMockUser
    void shouldAddProductToBasket() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        Resource resourceProduct = resourceLoader.getResource("classpath:files/product_by_id_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        SCHEMA_REGISTRY_SERVER.stubFor(post(urlEqualTo("/subjects/statistic-value/versions?normalize=false"))
                .willReturn(okJson("{}")));

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        PRODUCT_SERVER.stubFor(get(urlEqualTo("/api/v1/products/13"))
                .willReturn(okJson(Files.readString(Paths.get(resourceProduct.getURI())))));

        mockMvc.perform(patch("/api/v1/baskets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BasketDto.builder()
                                .productId(13L)
                                .quantity(45L)
                                .build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldNotAddProductToBasketWhenDoesNotExist() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        PRODUCT_SERVER.stubFor(get(urlEqualTo("/api/v1/products/13"))
                .willReturn(notFound()));

        mockMvc.perform(patch("/api/v1/baskets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BasketDto.builder()
                                .productId(13L)
                                .quantity(45L)
                                .build()
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldDeleteProductFromBasket() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        mockMvc.perform(delete("/api/v1/baskets/13")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldNotDeleteProductFromBasketDoesNotExist() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        basketRepository.save(Basket.builder()
                .product(Product.builder()
                        .name("ABCD")
                        .code("DBCD")
                        .price(13.13)
                        .quantity(20L)
                        .id(7L)
                        .imagePath("http")
                        .build())
                .userId(13L)
                .build());

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        mockMvc.perform(delete("/api/v1/baskets/13")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //idepotentność
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldClearBasketWhenBasketIsEmpty() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        mockMvc.perform(delete("/api/v1/baskets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldClearBasket() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        basketRepository.save(Basket.builder()
                .product(Product.builder()
                        .name("ABCD")
                        .code("DBCD")
                        .price(13.13)
                        .quantity(20L)
                        .id(7L)
                        .imagePath("http")
                        .build())
                .userId(13L)
                .build());

        basketRepository.save(Basket.builder()
                .product(Product.builder()
                        .name("Product2")
                        .code("ZTM")
                        .price(13.13)
                        .quantity(20L)
                        .id(10L)
                        .imagePath("http1")
                        .build())
                .userId(13L)
                .build());

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        mockMvc.perform(delete("/api/v1/baskets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldReturnProductsFromBasket() throws Exception {

        Resource resourceUser = resourceLoader.getResource("classpath:files/current_user_ok.json");
        String token = tokenTestHelperClass.generateToken("pablito", "ADMIN");

        USER_SERVER.stubFor(get(urlEqualTo("/api/v1/users/current"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token))
                .willReturn(okJson(Files.readString(Paths.get(resourceUser.getURI())))));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/baskets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
}
