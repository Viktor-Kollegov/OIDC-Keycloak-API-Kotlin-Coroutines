package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;


@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setRequestMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/oauth2/authorization/**"),
                new AntPathRequestMatcher("/login/oauth2/code/**")
        ));
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "register",
                                "/error",
                                "register-error",
                                "register-success",
                                "/.well-known/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/protected", true)
                        .failureUrl("/error")
                        .failureHandler((request, response, exception) -> {
                            request.setAttribute("error", "OAuth2 error: " + exception.getMessage());
                            response.sendRedirect("/error");
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
                );

        return http.build();
    }

}
