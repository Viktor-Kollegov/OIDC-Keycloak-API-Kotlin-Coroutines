package com.example.controller.api

import com.example.dto.AccountCreationRequest
import com.example.model.Account
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import java.math.BigDecimal

interface TransactionControllerApi {

    @Operation(summary = "Create a new account", description = "Creates an account with the specified currency")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Account successfully created"),
                ApiResponse(responseCode = "422", description = "Invalid currency or parameters"),
                ApiResponse(responseCode = "424", description = "Access denied")
            ]
    )
    suspend fun createAccount(request: AccountCreationRequest, jwt: Jwt): ResponseEntity<Account>

    @Operation(summary = "Deposit into account", description = "Adds the specified amount to the account")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Deposit successful"),
                ApiResponse(responseCode = "422", description = "Invalid amount or account ID"),
                ApiResponse(responseCode = "424", description = "Access denied")
            ]
    )
    suspend fun deposit(accountId: Long, amount: BigDecimal, jwt: Jwt): ResponseEntity<Void>

    @Operation(summary = "Withdraw from account", description = "Withdraws the specified amount from the account")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Withdrawal successful"),
                ApiResponse(responseCode = "422", description = "Insufficient funds or invalid account ID"),
                ApiResponse(responseCode = "424", description = "Access denied")
            ]
    )
    suspend fun withdraw(accountId: Long, amount: BigDecimal, jwt: Jwt): ResponseEntity<Void>

    @Operation(summary = "Get account balance", description = "Returns the current balance and currency for the account")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Balance successfully retrieved"),
                ApiResponse(responseCode = "501", description = "Account not found"),
                ApiResponse(responseCode = "424", description = "Access denied")
            ]
    )
    suspend fun getBalance(accountId: Long, jwt: Jwt): ResponseEntity<Map<String, Any>>

    @Operation(summary = "Get list of user accounts", description = "Returns the list of all accounts for the user")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Accounts successfully retrieved"),
                ApiResponse(responseCode = "424", description = "Access denied"),
                ApiResponse(responseCode = "500", description = "Internal server error")
            ]
    )
    suspend fun getUserAccounts(jwt: Jwt): ResponseEntity<List<Account>>
}
