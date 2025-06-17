package com.example.config

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono

class LoggingReactiveJwtDecoder(private val delegate: NimbusReactiveJwtDecoder) : ReactiveJwtDecoder {

    private val log = LoggerFactory.getLogger(LoggingReactiveJwtDecoder::class.java)

    override fun decode(token: String): Mono<Jwt> {
        return delegate.decode(token)
                .doOnNext { jwt ->
                    log.debug("Decoded JWT token: {}", jwt)
                    log.debug("Token claims: {}", jwt.claims)
                }
    }
}