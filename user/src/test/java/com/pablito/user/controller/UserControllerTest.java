package com.pablito.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.pablito.user.domain.dto.UserDto;
import com.pablito.user.repository.RoleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserControllerTest {

    private static final WireMockServer NOTIFICATION_SERVER = new WireMockServer(8084);

    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    static {
        MY_SQL_CONTAINER.start();
    }

    @BeforeAll
    static void beforeAll() {
        NOTIFICATION_SERVER.start();
    }

    @AfterAll
    static void afterAll() {
        NOTIFICATION_SERVER.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
    }

    @Test
    void shouldSaveUser() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDto.builder()
                                .email("abcd@interia.pl")
                                .firstName("PAN")
                                .lastName("PIŁKARZ")
                                .password("kukuryku")
                                .confirmedPassword("kukuryku")
                                .username("kokoszka")
                                .build()
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("kokoszka"))
                .andExpect(jsonPath("$.firstName").value("PAN"))
                .andExpect(jsonPath("$.lastName").value("PIŁKARZ"))
                .andExpect(jsonPath("$.email").value("abcd@interia.pl"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.confirmedPassword").doesNotExist());
    }
}
