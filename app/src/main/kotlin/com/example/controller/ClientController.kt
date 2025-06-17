package com.example.controller

import com.example.controller.api.ClientControllerApi
import com.example.dto.AccountCreationRequest
import io.swagger.v3.oas.annotations.Parameter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ModelAndView
import java.math.BigDecimal


@Controller
class ClientController(
        private val restTemplate: RestTemplate,
        @Value("\${resource.server.url}") private val resourceServerUrl: String
) : ClientControllerApi {

    private val log = LoggerFactory.getLogger(ClientController::class.java)

    @GetMapping("/protected")
    override fun protectedPage(
            @RegisteredOAuth2AuthorizedClient("keycloak") authorizedClient: OAuth2AuthorizedClient
    ): ModelAndView {
        val accessToken = authorizedClient.accessToken.tokenValue
        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val request = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(
                "$resourceServerUrl/api/accounts", HttpMethod.GET, request, List::class.java
        )
        val accounts = response.body
        return ModelAndView("protected").apply {
            addObject("accounts", accounts)
        }
    }

    @PostMapping("/create-account")
    override fun createAccount(
            @Parameter(description = "Валюта счёта (USD, EUR, RUB)", required = true)
            @RequestParam currency: String,
            @RegisteredOAuth2AuthorizedClient("keycloak") authorizedClient: OAuth2AuthorizedClient
    ): String {
        val accessToken = authorizedClient.accessToken.tokenValue
        log.info("Access Token for create-account: {}", accessToken)
        val headers = HttpHeaders().apply {
            setBearerAuth(accessToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val body = AccountCreationRequest(currency)
        val request = HttpEntity(body, headers)
        restTemplate.exchange(
                "$resourceServerUrl/api/accounts", HttpMethod.POST, request, Void::class.java
        )
        return "redirect:/protected"
    }

    @PostMapping("/deposit")
    override fun deposit(
            @Parameter(description = "ID счёта", required = true)
            @RequestParam accountId: Long,
            @Parameter(description = "Сумма для пополнения", required = true)
            @RequestParam amount: BigDecimal,
            @RegisteredOAuth2AuthorizedClient("keycloak") authorizedClient: OAuth2AuthorizedClient
    ): String {
        val accessToken = authorizedClient.accessToken.tokenValue
        val headers = HttpHeaders().apply {
            setBearerAuth(accessToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val request = HttpEntity(amount, headers)
        restTemplate.exchange(
                "$resourceServerUrl/api/accounts/$accountId/deposit", HttpMethod.POST, request, Void::class.java
        )
        return "redirect:/protected"
    }

    @PostMapping("/withdraw")
    override fun withdraw(
            @Parameter(description = "ID счёта", required = true)
            @RequestParam accountId: Long,
            @Parameter(description = "Сумма для снятия", required = true)
            @RequestParam amount: BigDecimal,
            @RegisteredOAuth2AuthorizedClient("keycloak") authorizedClient: OAuth2AuthorizedClient
    ): String {
        val accessToken = authorizedClient.accessToken.tokenValue
        val headers = HttpHeaders().apply {
            setBearerAuth(accessToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val request = HttpEntity(amount, headers)
        restTemplate.exchange(
                "$resourceServerUrl/api/accounts/$accountId/withdraw", HttpMethod.POST, request, Void::class.java
        )
        return "redirect:/protected"
    }

    @GetMapping("/balance")
    override fun getBalance(
            @Parameter(description = "ID счёта", required = true)
            @RequestParam accountId: Long,
            @RegisteredOAuth2AuthorizedClient("keycloak") authorizedClient: OAuth2AuthorizedClient
    ): ModelAndView {
        val accessToken = authorizedClient.accessToken.tokenValue
        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val request = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(
                "$resourceServerUrl/api/accounts/$accountId/balance", HttpMethod.GET, request, Map::class.java
        )
        val balanceData = response.body ?: emptyMap<String, Any>()
        val balance = BigDecimal(balanceData["balance"].toString())
        val currency = balanceData["currency"] as String
        return ModelAndView("balance").apply {
            addObject("accountId", accountId)
            addObject("balance", balance)
            addObject("currency", currency)
        }
    }
}