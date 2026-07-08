package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rozgrywkiLigoweOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rozgrywki Ligowe API")
                        .version("1.0")
                        .description("Dokumentacja REST API dla aplikacji Rozgrywki Ligowe"));
    }
}
