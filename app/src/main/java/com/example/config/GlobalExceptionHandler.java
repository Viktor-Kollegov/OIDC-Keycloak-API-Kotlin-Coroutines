package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpServerErrorException.class)
    public String handleServerError(HttpServerErrorException e) {
        log.error("Server error from resource server: {}", e.getMessage(), e);
        String userMessage = "Произошла ошибка на сервере. Пожалуйста, попробуйте снова позже.";
        try {
            return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Error encoding message: {}", ex.getMessage(), ex);
            return "redirect:/error?message=Unexpected+error";
        }
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleClientError(HttpClientErrorException e) {
        log.error("Client error from resource server: {}", e.getMessage(), e);
        String userMessage = switch (e.getStatusCode().value()) {
            case 400 -> "Некорректный запрос. Проверьте введённые данные.";
            case 401 -> "Требуется повторная авторизация.";
            case 403 -> "Доступ запрещён. У вас недостаточно прав.";
            default -> "Ошибка при выполнении запроса. Попробуйте снова.";
        };
        try {
            return "redirect:/error?message=" + URLEncoder.encode(userMessage, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Error encoding message: {}", ex.getMessage(), ex);
            return "redirect:/error?message=Unexpected+error";
        }
    }

}
