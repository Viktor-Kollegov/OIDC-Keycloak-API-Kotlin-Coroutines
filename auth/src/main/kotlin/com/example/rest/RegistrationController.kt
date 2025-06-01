package com.example.rest

import com.example.dto.RegistrationRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.web.bind.annotation.*

@RestController
class RegistrationController(
        private val users: UserDetailsManager,
        private val encoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(RegistrationController::class.java)

    @PostMapping("/register")
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<String> {
        log.info("User registration process established for user {}", request.username)

        if (request.username.isNullOrBlank() || request.password.isNullOrBlank()) {
            return ResponseEntity.badRequest().body("Username and password must not be null")
        }

        if (users.userExists(request.username)) {
            return ResponseEntity.badRequest().body("User already exists")
        }

        val user = User.withUsername(request.username)
                .password(encoder.encode(request.password))
                .roles("USER")
                .build()

        users.createUser(user)
        log.info("User registered: {}", user.username)
        return ResponseEntity.ok("User registered")
    }
}
