package com.example.controller;

import com.example.dto.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
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
    private final String registrationEndpoint = "http://localhost:9000/register";

    public RegistrationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
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

