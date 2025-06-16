package com.example.config

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt


class MultiIssuerJwtValidator(private val allowedIssuers: Set<String>) : OAuth2TokenValidator<Jwt> {
    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
        val issuer = jwt.getClaimAsString("iss")
        return if (issuer == null || !allowedIssuers.contains(issuer)) {
            val description = "Invalid issuer: $issuer. Allowed issuers: $allowedIssuers"
            OAuth2TokenValidatorResult.failure(
                    OAuth2Error("invalid_token", description, null)
            )
        } else OAuth2TokenValidatorResult.success()
    }
}