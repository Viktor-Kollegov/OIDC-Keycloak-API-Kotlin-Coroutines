package com.example.controller;

import com.example.dto.RegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@Controller
@Slf4j
public class RegistrationController {

    private final RestTemplate restTemplate;
    private final String registrationEndpoint;

    public RegistrationController(RestTemplate restTemplate, @Value("${auth.server.url}/register") String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создаёт новый аккаунт пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на /register-success после успешной регистрации"),
            @ApiResponse(responseCode = "500", description = "Перенаправление на /register-error после ошибки регистрации")
    })
    public String register(@RequestBody RegistrationRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegistrationRequest> httpRequest = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(registrationEndpoint, httpRequest, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/register-success";
            } else {
                log.error("Registration error, {}", response.getStatusCode());
                return "redirect:/register-error";
            }
        } catch (Exception e) {
            log.error("Registration error, {}", e.getMessage());
            return "redirect:/register-error";
        }
    }

    @GetMapping("/register-success")
    public String registrationSuccess() {
        return "register-success";
    }

    @GetMapping("/register-error")
    public String registrationError() {
        return "register-error";
    }

}

