package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http
                .csrf { csrf ->
                    csrf.ignoringRequestMatchers(
                            "/oauth2/token",
                            "/oauth2/authorize",
                            "/oauth2/jwks"
                    )
                }
                .exceptionHandling { exceptions ->
                    exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
                }
        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurity(http: HttpSecurity): SecurityFilterChain {
        val requestCache = HttpSessionRequestCache().apply {
            setRequestMatcher { request ->
                request.requestURI.startsWith("/oauth2")
            }
        }

        http
                .authorizeHttpRequests { authorize ->
                    authorize
                            .requestMatchers(
                                    "/register",
                                    "/register-success",
                                    "/h2-console/**",
                                    "/login",
                                    "/error"
                            ).permitAll()
                            .anyRequest().authenticated()
                }
                .formLogin(Customizer.withDefaults())
                .requestCache { cache ->
                    cache.requestCache(requestCache)
                }
                .csrf { csrf ->
                    csrf.ignoringRequestMatchers("/h2-console/**", "/register")
                }
                .headers { headers ->
                    headers.frameOptions().disable() // for H2 only
                }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun jdbcUserDetailsManager(dataSource: DataSource): UserDetailsManager {
        return JdbcUserDetailsManager(dataSource)
    }
}
