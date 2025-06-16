package com.example.controller

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TokenController(private val authorizedClientService: OAuth2AuthorizedClientService) {
    @GetMapping("/token")
    fun getToken(authentication: OAuth2AuthenticationToken): String {
        val client = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
                authentication.authorizedClientRegistrationId,
                authentication.name)
        return client.accessToken.tokenValue
    }
}