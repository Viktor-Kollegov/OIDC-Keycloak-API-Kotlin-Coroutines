package com.example.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(RuntimeException::class)
    fun handleIllegalState(e: java.lang.RuntimeException): ResponseEntity<Map<String, String>> {
        log.error("RuntimeException: {}", e.message, e)
        val body = mapOf("error" to (e.message ?: "Unknown internal error"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

}
