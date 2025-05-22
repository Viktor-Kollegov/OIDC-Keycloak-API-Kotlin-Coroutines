package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI clientApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Client API")
                        .description("API для управления счетами через клиентское приложение")
                        .version("1.0.0")
                        .license(new License().name("Proprietary").url("https://github.com/Viktor-Kollegov/PKCE-OAuth-API/blob/main/LICENSE")));
    }

}
