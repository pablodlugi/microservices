package com.pablito.product.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("BEARER AUT TOKEN", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("USER API")
                        .description("TO DO")
                        .version("1.0.0")
                        .termsOfService("")
                        .contact(new Contact()
                                .email("dluskipawel13@gmail.com")
                                .name("Paweł Dłuski")
                                .url("https://github.com/pablodlugi")));
    }
}
