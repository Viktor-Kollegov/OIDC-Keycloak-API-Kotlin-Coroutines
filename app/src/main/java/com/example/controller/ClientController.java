package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ModelAndView protectedPage(@RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    resourceServerUrl + "/api/accounts", HttpMethod.GET, request, List.class);
            List accounts = response.getBody();
            ModelAndView mav = new ModelAndView("protected");
            mav.addObject("accounts", accounts);
            return mav;
        } catch (Exception e) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", "Не удалось получить счета: " + e.getMessage());
            return mav;
        }
    }

    @PostMapping("/create-account")
    public String createAccount(
            @RequestParam String currency,
            @RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("currency", currency);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(
                    resourceServerUrl + "/api/accounts",
                    HttpMethod.POST,
                    request,
                    Void.class);
            return "redirect:/protected";
        } catch (Exception e) {
            String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/error?message=" + encodedMessage;
        }
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BigDecimal> request = new HttpEntity<>(amount, headers);
        try {
            restTemplate.exchange(
                    resourceServerUrl + "/api/accounts/" + accountId + "/deposit",
                    HttpMethod.POST,
                    request,
                    Void.class);
            return "redirect:/protected";
        } catch (Exception e) {
            String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/error?message=" + encodedMessage;
        }
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BigDecimal> request = new HttpEntity<>(amount, headers);
        try {
            restTemplate.exchange(
                    resourceServerUrl + "/api/accounts/" + accountId + "/withdraw",
                    HttpMethod.POST,
                    request,
                    Void.class);
            return "redirect:/protected";
        } catch (Exception e) {
            String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/error?message=" + encodedMessage;
        }
    }

    @GetMapping("/balance")
    public ModelAndView getBalance(
            @RequestParam Long accountId,
            @RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    resourceServerUrl + "/api/accounts/" + accountId + "/balance",
                    HttpMethod.GET,
                    request,
                    Map.class);
            Map<String, Object> balanceData = response.getBody();
            BigDecimal balance = new BigDecimal(balanceData.get("balance").toString());
            String currency = (String) balanceData.get("currency");
            ModelAndView mav = new ModelAndView("balance");
            mav.addObject("accountId", accountId);
            mav.addObject("balance", balance);
            mav.addObject("currency", currency);
            return mav;
        } catch (Exception e) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", "Не удалось получить баланс: " + e.getMessage());
            return mav;
        }
    }

}
