package com.example.config

import com.example.service.CustomOAuth2UserService
import com.example.service.CustomOidcUserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.client.RestTemplate

@Configuration
class ClientConfiguration {

    @Bean
    @Qualifier("loggedRestTemplate")
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()
        restTemplate.interceptors.add(LoggingInterceptor())
        return restTemplate
    }

    @Bean
    @Qualifier("customOAuth2UserService")
    fun oAuth2UserService(@Qualifier("loggedRestTemplate") restTemplate: RestTemplate): OAuth2UserService<OAuth2UserRequest, OAuth2User> =
            CustomOAuth2UserService(restTemplate)

    @Bean
    @Qualifier("customOidcUserService")
    fun userService(@Qualifier("customOAuth2UserService") oauth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User>): OAuth2UserService<OidcUserRequest, OidcUser> {
        return CustomOidcUserService(oauth2UserService);
    }

}
