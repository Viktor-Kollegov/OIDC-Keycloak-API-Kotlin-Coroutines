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

    @Operation(summary = "Создать новый счёт", description = "Создаёт счёт с указанной валютой")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Счёт успешно создан"),
                ApiResponse(responseCode = "400", description = "Некорректный запрос"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    suspend fun createAccount(request: AccountCreationRequest, jwt: Jwt): ResponseEntity<Account>

    @Operation(summary = "Пополнить счёт", description = "Добавляет указанную сумму на счёт")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Счёт успешно пополнен"),
                ApiResponse(responseCode = "400", description = "Некорректная сумма или счёт"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    suspend fun deposit(accountId: Long, amount: BigDecimal, jwt: Jwt): ResponseEntity<Void>

    @Operation(summary = "Снять средства со счёта", description = "Снимает указанную сумму со счёта")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Средства успешно сняты"),
                ApiResponse(responseCode = "400", description = "Недостаточно средств или некорректный счёт"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    suspend fun withdraw(accountId: Long, amount: BigDecimal, jwt: Jwt): ResponseEntity<Void>

    @Operation(summary = "Получить баланс счёта", description = "Возвращает текущий баланс и валюту счёта")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
                ApiResponse(responseCode = "400", description = "Счёт не найден"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    suspend fun getBalance(accountId: Long, jwt: Jwt): ResponseEntity<Map<String, Any>>

    @Operation(summary = "Получить список счетов", description = "Возвращает список счетов пользователя")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Список счетов успешно получен"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    suspend fun getUserAccounts(jwt: Jwt): ResponseEntity<List<Account>>
}
