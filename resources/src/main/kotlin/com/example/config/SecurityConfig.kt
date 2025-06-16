package com.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
class SecurityConfig(
        @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
        private val issuerUri: String
) {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .authorizeExchange {
                    it.pathMatchers("/api/**").authenticated()
                            .anyExchange().permitAll()
                }
                .oauth2ResourceServer { rs ->
                    rs.jwt { jwt -> jwt.jwtDecoder(jwtDecoder()) }
                }
                .build()
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return ReactiveJwtDecoders.fromIssuerLocation(issuerUri)
    }

}
