package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ClientController {
    private final RestTemplate restTemplate;
    private final String resourceServerUrl;

    public ClientController(RestTemplate restTemplate,
                            @Value("${resource.server.url:http://127.0.0.1:8081}") String resourceServerUrl) {
        this.restTemplate = restTemplate;
        this.resourceServerUrl = resourceServerUrl;
    }

    @GetMapping("/protected")
    public ModelAndView accessProtectedResource(
            @RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        log.info("Accessing protected resource with token: {}", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    resourceServerUrl + "/api/protected", HttpMethod.GET, request, String.class);
            log.info("Received resource response: {}", response.getBody());

            ModelAndView mav = new ModelAndView("result");
            mav.addObject("response", response.getBody());
            return mav;
        } catch (Exception e) {
            log.error("Resource access failed: {}", e.getMessage(), e);
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", "Failed to access resource: " + e.getMessage());
            return mav;
        }
    }

}
