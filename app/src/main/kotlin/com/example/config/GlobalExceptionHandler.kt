package com.example.config

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(HttpServerErrorException::class)
    fun handleServerError(e: HttpServerErrorException): String {
        log.error("Server error from resource server: {}", e.message, e)
        val userMessage = "Resource server error. ${e.message}"
        return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleClientError(e: HttpClientErrorException): String {
        log.error("Client error from resource server: {}", e.message, e)
        val userMessage = when (e.statusCode.value()) {
            400 -> "Invalid request. Please check the data you entered."
            401 -> "Re-authentication required."
            403 -> "Access denied. You do not have sufficient permissions."
            else -> "An error occurred during the request. Please try again."
        }
        return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
    }
}
