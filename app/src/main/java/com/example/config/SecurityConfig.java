package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.Objects;


@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public HttpSessionRequestCache requestCache() {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setRequestMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/oauth2/authorization/**"),
                new AntPathRequestMatcher("/login/oauth2/code/**"),
                new AntPathRequestMatcher("/protected**")
        ));
        return requestCache;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HttpSessionRequestCache requestCache, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/protected", true)
                        .failureHandler((request, response, exception) -> {
                            request.setAttribute("error", "OAuth2 error: " + exception.getMessage());
                            log.error("OAuth2 ERROR MESSAGE: {}", exception.getMessage());
                            if (Objects.equals(request.getSession().getId(), null)) {
                                log.warn("SESSION ID IS NULL");
                                response.sendRedirect("/oauth2/authorization/transactions-api");
                            } else {
                                response.sendRedirect("/error");
                            }
                        })
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/register")
                )
                .requestCache(cache -> cache
                        .requestCache(requestCache)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/.well-known/**");
    }

}
