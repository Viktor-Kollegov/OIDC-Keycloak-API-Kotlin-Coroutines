package com.example.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher

@Configuration
class SecurityConfig {

    private val log = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Bean
    fun requestCache(): HttpSessionRequestCache {
        val requestCache = HttpSessionRequestCache()
        requestCache.setRequestMatcher(
                OrRequestMatcher(
                        AntPathRequestMatcher("/oauth2/authorization/**"),
                        AntPathRequestMatcher("/login/oauth2/code/**"),
                        AntPathRequestMatcher("/protected**")
                )
        )
        return requestCache
    }

    @Bean
    fun securityFilterChain(
            http: HttpSecurity,
            requestCache: HttpSessionRequestCache,
            oAuth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User>
    ): SecurityFilterChain {
        http
                .authorizeHttpRequests { auth ->
                    auth.anyRequest().permitAll()
                }
                .oauth2Login { oauth2 ->
                    oauth2
                            .defaultSuccessUrl("/protected", true)
                            .failureHandler { request, response, exception ->
                                request.setAttribute("error", "OAuth2 error: ${exception.message}")
                                log.error("OAuth2 ERROR MESSAGE: {}", exception.message)
                                if (request.session?.id == null) {
                                    log.warn("SESSION ID IS NULL")
                                    response.sendRedirect("/oauth2/authorization/transactions-api")
                                } else {
                                    response.sendRedirect("/error")
                                }
                            }
                            .userInfoEndpoint { userInfo ->
                                userInfo.userService(oAuth2UserService)
                            }
                }
                .csrf { csrf ->
                    csrf.ignoringRequestMatchers(AntPathRequestMatcher("/register"))
                }
                .requestCache { cache ->
                    cache.requestCache(requestCache)
                }
                .sessionManagement { session ->
                    session
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .maximumSessions(1)
                            .expiredUrl("/login?expired")
                }

        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(AntPathRequestMatcher("/.well-known/**"))
        }
    }
}
