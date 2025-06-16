package com.example.service

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User


class CustomOidcUserService(oauth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User>) : OidcUserService() {

    private val log = LoggerFactory.getLogger(CustomOidcUserService::class.java)

    init {
        setOauth2UserService(oauth2UserService)
    }

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        log.warn("Access Token: ${userRequest.accessToken.tokenValue}")
        log.warn("Scopes: ${userRequest.accessToken.scopes}")
        return super.loadUser(userRequest)
    }
}