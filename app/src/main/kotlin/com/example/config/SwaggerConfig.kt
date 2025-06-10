package com.example.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun clientApi(): OpenAPI {
        return OpenAPI().info(
                Info()
                        .title("Client API")
                        .description("API for managing accounts via a client application")
                        .version("1.0.0")
                        .license(
                                License()
                                        .name("Proprietary")
                                        .url("https://github.com/Viktor-Kollegov/PKCE-OAuth-API-Kotlin-Coroutines/blob/main/LICENSE")
                        )
        )
    }
}
