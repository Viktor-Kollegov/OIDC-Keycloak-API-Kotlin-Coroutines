package com.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
class SecurityConfig(
        @Value("\${auth.server.url}") private val authServerUrl: String
) {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .authorizeExchange { exchanges ->
                    exchanges
                            .pathMatchers("/api/accounts/**").authenticated()
                            .anyExchange().permitAll()
                }
                .oauth2ResourceServer { oauth2 ->
                    oauth2.jwt { jwt ->
                        jwt.jwkSetUri("$authServerUrl/oauth2/jwks")
                    }
                }
                .build()
    }
}
