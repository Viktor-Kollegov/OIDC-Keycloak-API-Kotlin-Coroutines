package com.example.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
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
        log.error("💥 Server error from resource server: {}", e.message, e)

        val rawMessage = extractJsonMessage(e.responseBodyAsString)
        val userMessage = "💥 ${HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase}: $rawMessage"

        return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleClientError(e: HttpClientErrorException): String {
        log.error("⚠️ Client error from resource server: {}", e.message, e)

        val status = e.statusCode
        val rawMessage = extractJsonMessage(e.responseBodyAsString)
        val userMessage = when (status) {
            HttpStatus.BAD_REQUEST -> "⚠️ Bad request. $rawMessage"
            HttpStatus.UNAUTHORIZED -> "🔒 Re-authentication required. $rawMessage"
            HttpStatus.FORBIDDEN -> "🚫 Access denied. $rawMessage"
            else -> "❗ Unexpected error (${status.value()}). $rawMessage"
        }

        return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
    }

    private fun extractJsonMessage(responseBody: String): String {
        val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
        return regex.find(responseBody)?.groupValues?.get(1) ?: "No details provided"
    }
}
