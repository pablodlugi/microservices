package com.example.notification.controller;

import com.example.notification.dao.Template;
import com.example.notification.dto.TemplateDto;
import com.example.notification.repository.TemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class TemplateControllerTest {

    private static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:5.0.9"));

    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    static {
        MONGODB_CONTAINER.start();
        MY_SQL_CONTAINER.start();
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TemplateRepository templateRepository;


    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", MONGODB_CONTAINER::getReplicaSetUrl);
        dynamicPropertyRegistry.add("quartzDataSource.url", MY_SQL_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("quartzDataSource.user", MY_SQL_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("quartzDataSource.password", MY_SQL_CONTAINER::getPassword);
    }


    @Test
    void shouldSaveTemplate() throws Exception {

        webTestClient.post().uri("/api/v1/templates")
                .body(Mono.just(TemplateDto.builder()
                        .name("Notification")
                        .body("<p>Notification</p>")
                        .subject("Click here")
                        .build()), TemplateDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Notification")
                .jsonPath("$.id").exists()
                .jsonPath("$.body").isEqualTo("<p>Notification</p>")
                .jsonPath("$.subject").isEqualTo("Click here");
    }

    @Test
    void shouldGetTemplateByName() {

        Template template = templateRepository.save(Template.builder()
                .name("Notification")
                .subject("Click here")
                .body("<p>Notification</p>")
                .build()).block();

        webTestClient.get().uri("/api/v1/templates?name=Notification")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Notification")
                .jsonPath("$.subject").isEqualTo("Click here")
                .jsonPath("$.body").isEqualTo("<p>Notification</p>");
    }

    @Test
    void shouldNotReturnTemplateWhenDoesNotExist() {

        webTestClient.get().uri("/api/v1/templates?name=Notification")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
    }


    @Test
    void shouldUpdateTemplate() {

        Template template = templateRepository.save(Template.builder()
                .name("Notification")
                .subject("Confirmation")
                .body("<p>Notification</p>")
                .build()).block();

        webTestClient.put().uri("/api/v1/templates")
                .body(Mono.just(TemplateDto.builder()
                        .name("Notification")
                        .body("<p>Notification</p>")
                        .subject("Click here")
                        .build()), TemplateDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void shouldNotUpdateTemplateWhenDoesNotExist() {

        webTestClient.put().uri("/api/v1/templates")
                .body(Mono.just(TemplateDto.builder()
                        .name("Notification")
                        .body("<p>Notification</p>")
                        .subject("Click here")
                        .build()), TemplateDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void shouldDeleteTemplate() {

        Template template = templateRepository.save(Template.builder()
                .id("111")
                .name("Notification")
                .subject("Confirmation")
                .body("<p>Notification</p>")
                .build()).block();

        webTestClient.delete().uri("/api/v1/templates/111")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void shouldNotDeleteTemplateWhenDoesNotExist() {

        Template template = templateRepository.save(Template.builder()
                .id("111")
                .name("Notification")
                .subject("Confirmation")
                .body("<p>Notification</p>")
                .build()).block();

        webTestClient.delete().uri("/api/v1/templates/112")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }
}
