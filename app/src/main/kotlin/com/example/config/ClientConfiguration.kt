package com.example.config

import com.example.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.client.RestTemplate

@Configuration
class ClientConfiguration {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()

    @Bean
    fun oAuth2UserService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> =
            CustomOAuth2UserService()
}
