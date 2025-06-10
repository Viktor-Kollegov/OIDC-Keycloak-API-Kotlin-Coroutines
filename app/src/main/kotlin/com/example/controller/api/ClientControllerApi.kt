package com.example.controller.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import java.math.BigDecimal
import org.springframework.web.servlet.ModelAndView

interface ClientControllerApi {

    @Operation(summary = "Get list of accounts", description = "Returns the list of all user accounts")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Accounts list successfully retrieved"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    fun protectedPage(authorizedClient: OAuth2AuthorizedClient): ModelAndView

    @Operation(summary = "Create a new account", description = "Creates an account with the specified currency")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "302", description = "Redirect to /protected after successful creation"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    fun createAccount(currency: String, authorizedClient: OAuth2AuthorizedClient): String

    @Operation(summary = "Deposit into account", description = "Adds the specified amount to the account")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "302", description = "Redirect to /protected after successful deposit"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    fun deposit(accountId: Long, amount: BigDecimal, authorizedClient: OAuth2AuthorizedClient): String

    @Operation(summary = "Withdraw from account", description = "Withdraws the specified amount from the account")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "302", description = "Redirect to /protected after successful withdrawal"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    fun withdraw(accountId: Long, amount: BigDecimal, authorizedClient: OAuth2AuthorizedClient): String

    @Operation(summary = "Get account balance", description = "Returns the current account balance and currency")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Balance successfully retrieved"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    fun getBalance(accountId: Long, authorizedClient: OAuth2AuthorizedClient): ModelAndView
}
