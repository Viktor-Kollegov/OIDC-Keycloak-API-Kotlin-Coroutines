package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Получить список счетов", description = "Возвращает список всех счетов пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список счетов успешно получен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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
            log.error("Exception in protectedPage: {}", e.getMessage(), e);
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", "Не удалось получить счета: " + e.getMessage());
            return mav;
        }
    }

    @PostMapping("/create-account")
    @Operation(summary = "Создать новый счёт", description = "Создаёт счёт с указанной валютой")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на /protected после успешного создания"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public String createAccount(
            @Parameter(description = "Валюта счёта (USD, EUR, RUB)", required = true) @RequestParam String currency,
            @RegisteredOAuth2AuthorizedClient("transactions-api") OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        log.info("Access Token for create-account: {}", accessToken);
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
            log.error("Exception in createAccount: {}", e.getMessage(), e);
            return "redirect:/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/deposit")
    @Operation(summary = "Пополнить счёт", description = "Добавляет указанную сумму на счёт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на /protected после успешного пополнения"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public String deposit(
            @Parameter(description = "ID счёта", required = true) @RequestParam Long accountId,
            @Parameter(description = "Сумма для пополнения", required = true) @RequestParam BigDecimal amount,
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
            log.error("Exception in deposit: {}", e.getMessage(), e);
            return "redirect:/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Снять средства со счёта", description = "Снимает указанную сумму со счёта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на /protected после успешного снятия"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public String withdraw(
            @Parameter(description = "ID счёта", required = true) @RequestParam Long accountId,
            @Parameter(description = "Сумма для снятия", required = true) @RequestParam BigDecimal amount,
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
            log.error("Exception in withdraw: {}", e.getMessage(), e);
            return "redirect:/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/balance")
    @Operation(summary = "Получить баланс счёта", description = "Возвращает текущий баланс и валюту счёта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ModelAndView getBalance(
            @Parameter(description = "ID счёта", required = true) @RequestParam Long accountId,
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
            log.error("Exception in getBalance: {}", e.getMessage(), e);
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", "Не удалось получить баланс: " + e.getMessage());
            return mav;
        }
    }
}
