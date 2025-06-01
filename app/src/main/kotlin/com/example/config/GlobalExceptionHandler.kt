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
        val userMessage = "Произошла ошибка на сервере. Пожалуйста, попробуйте снова позже."
        return try {
            "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            log.error("Error encoding message: {}", ex.message, ex)
            "redirect:/error?message=Unexpected+error"
        }
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleClientError(e: HttpClientErrorException): String {
        log.error("Client error from resource server: {}", e.message, e)
        val userMessage = when (e.statusCode.value()) {
            400 -> "Некорректный запрос. Проверьте введённые данные."
            401 -> "Требуется повторная авторизация."
            403 -> "Доступ запрещён. У вас недостаточно прав."
            else -> "Ошибка при выполнении запроса. Попробуйте снова."
        }
        return try {
            "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            log.error("Error encoding message: {}", ex.message, ex)
            "redirect:/error?message=Unexpected+error"
        }
    }
}
