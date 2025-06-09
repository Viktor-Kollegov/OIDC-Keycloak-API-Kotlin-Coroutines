package com.example.config

import com.example.dto.ErrorResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
    private val mapper = jacksonObjectMapper()

    @ExceptionHandler(HttpServerErrorException::class)
    fun handleServerError(e: HttpServerErrorException): String {
        log.error("💥 Server error: {}", e.message, e)

        val errorResponse = parseError(e.responseBodyAsString)
        val userMessage = "💥 ${HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase}: ${errorResponse?.message ?: "No details"}"

        return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleClientError(e: HttpClientErrorException): String {
        log.error("⚠️ Client error: {}", e.message, e)

        val status = e.statusCode
        val errorResponse = parseError(e.responseBodyAsString)

        val userMessage = when (status) {
            HttpStatus.BAD_REQUEST   -> "⚠️ Bad request. ${errorResponse?.message ?: ""}"
            HttpStatus.UNAUTHORIZED  -> "🔒 Re-authentication required. ${errorResponse?.message ?: ""}"
            HttpStatus.FORBIDDEN     -> "🚫 Access denied. ${errorResponse?.message ?: ""}"
            else                     -> "❗ Unexpected error (${status.value()}). ${errorResponse?.message ?: ""}"
        }

        return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
    }

    private fun parseError(json: String): ErrorResponse? {
        return try {
            mapper.readValue<ErrorResponse>(json)
        } catch (ex: Exception) {
            log.warn("❓ Unable to parse error response: {}", json, ex)
            null
        }
    }
}
