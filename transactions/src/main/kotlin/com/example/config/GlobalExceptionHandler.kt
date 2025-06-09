package com.example.config

import com.example.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<ErrorResponse> {
        log.error("❌ Account not found: {}", e.message)
        val status = HttpStatus.NOT_IMPLEMENTED
        val body = ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = e.message ?: "Account not found"
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.error("🚫 Access denied: {}", e.message)
        val status = HttpStatus.FAILED_DEPENDENCY
        val body = ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = e.message ?: "Access denied"
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalidAmount(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.error("⚠️ Invalid operation: {}", e.message)
        val status = HttpStatus.UNPROCESSABLE_ENTITY
        val body = ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = e.message ?: "Invalid argument"
        )
        return ResponseEntity.status(status).body(body)
    }
}

