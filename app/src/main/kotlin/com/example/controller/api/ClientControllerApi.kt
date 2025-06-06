package com.example.controller.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import java.math.BigDecimal
import org.springframework.web.servlet.ModelAndView

interface ClientControllerApi {

    @Operation(summary = "Получить список счетов", description = "Возвращает список всех счетов пользователя")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Список счетов успешно получен"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    fun protectedPage(authorizedClient: OAuth2AuthorizedClient): ModelAndView

    @Operation(summary = "Создать новый счёт", description = "Создаёт счёт с указанной валютой")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "302", description = "Перенаправление на /protected после успешного создания"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    fun createAccount(currency: String, authorizedClient: OAuth2AuthorizedClient): String

    @Operation(summary = "Пополнить счёт", description = "Добавляет указанную сумму на счёт")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "302", description = "Перенаправление на /protected после успешного пополнения"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    fun deposit(accountId: Long, amount: BigDecimal, authorizedClient: OAuth2AuthorizedClient): String

    @Operation(summary = "Снять средства со счёта", description = "Снимает указанную сумму со счёта")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "302", description = "Перенаправление на /protected после успешного снятия"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    fun withdraw(accountId: Long, amount: BigDecimal, authorizedClient: OAuth2AuthorizedClient): String

    @Operation(summary = "Получить баланс счёта", description = "Возвращает текущий баланс и валюту счёта")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    fun getBalance(accountId: Long, authorizedClient: OAuth2AuthorizedClient): ModelAndView
}
