package com.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
        @Value("\${auth.server.url}") private val authServerUrl: String
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .authorizeHttpRequests { authorize ->
                    authorize
                            .requestMatchers("/api/accounts**").authenticated()
                            .anyRequest().permitAll()
                }
                .oauth2ResourceServer { oauth2 ->
                    oauth2.jwt { jwt ->
                        jwt.jwkSetUri("$authServerUrl/oauth2/jwks")
                    }
                }
        return http.build()
    }
}
