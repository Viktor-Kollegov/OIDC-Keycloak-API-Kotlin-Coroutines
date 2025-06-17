package com.example.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
class SecurityConfig(
        @Value("\${keycloak.issuer.jwkSetUrl}")
        private val jwkSetUrl: String,
        @Value("\${keycloak.issuer.allowedIssuers}")
        private val allowedIssuersString: String
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
    @Qualifier("multiIssuerNimbusReactiveJwtDecoder")
    fun jwtDecoder(): ReactiveJwtDecoder {
        val decoder = NimbusReactiveJwtDecoder(jwkSetUrl)
        decoder.setJwtValidator(MultiIssuerJwtValidator(allowedIssuersString.split(",")))
        return decoder
    }

}
