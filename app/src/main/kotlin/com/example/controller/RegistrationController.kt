package com.example.controller

import com.example.dto.RegistrationRequest
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@Controller
class RegistrationController(
        private val restTemplate: RestTemplate,
        @Value("\${auth.server.url}/register") private val registrationEndpoint: String
) {

    private val log = LoggerFactory.getLogger(RegistrationController::class.java)

    @GetMapping("/")
    fun home(): String = "redirect:/register"

    @GetMapping("/register")
    fun register(): String = "register"

    @PostMapping("/register")
    @Operation(summary = "New User Registration", description = "Creates a new user account")
    fun register(@RequestBody request: RegistrationRequest): String {
        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            val httpRequest = HttpEntity(request, headers)
            val response: ResponseEntity<String> = restTemplate.postForEntity(registrationEndpoint, httpRequest, String::class.java)

            if (response.statusCode.is2xxSuccessful) {
                "redirect:/register-success"
            } else {
                log.error("Registration error, {}", response.statusCode)
                "redirect:/register-error"
            }
        } catch (e: Exception) {
            log.error("Registration error, {}", e.message)
            "redirect:/register-error"
        }
    }

    @GetMapping("/register-success")
    fun registrationSuccess(): String = "register-success"

    @GetMapping("/register-error")
    fun registrationError(): String = "register-error"
}
