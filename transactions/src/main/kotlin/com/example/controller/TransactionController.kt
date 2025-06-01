package com.example.controller

import com.example.dto.AccountCreationRequest
import com.example.model.Account
import com.example.repository.AccountRepository
import com.example.service.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/api/accounts")
class TransactionController(
        private val transactionService: TransactionService,
        private val accountRepository: AccountRepository
) {

    @PostMapping
    @Operation(summary = "Создать новый счёт", description = "Создаёт счёт с указанной валютой")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Счёт успешно создан"),
                ApiResponse(responseCode = "400", description = "Некорректный запрос"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    fun createAccount(
            @RequestBody request: AccountCreationRequest,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Account> {
        val userId = jwt.subject
        val account = Account().apply {
            this.userId = userId
            this.currency = request.currency
        }
        accountRepository.save(account)
        return ResponseEntity.ok(account)
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "Пополнить счёт", description = "Добавляет указанную сумму на счёт")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Счёт успешно пополнен"),
                ApiResponse(responseCode = "400", description = "Некорректная сумма или счёт"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    fun deposit(
            @PathVariable accountId: Long,
            @RequestBody amount: BigDecimal,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        transactionService.deposit(accountId, amount, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "Снять средства со счёта", description = "Снимает указанную сумму со счёта")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Средства успешно сняты"),
                ApiResponse(responseCode = "400", description = "Недостаточно средств или некорректный счёт"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    fun withdraw(
            @PathVariable accountId: Long,
            @RequestBody amount: BigDecimal,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        transactionService.withdraw(accountId, amount, userId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Получить баланс счёта", description = "Возвращает текущий баланс и валюту счёта")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
                ApiResponse(responseCode = "400", description = "Счёт не найден"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён")
            ]
    )
    fun getBalance(
            @PathVariable accountId: Long,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Map<String, Any>> {
        val userId = jwt.subject
        val account = accountRepository.findById(accountId)
                .orElseThrow { EntityNotFoundException("Счет не найден") }

        if (account.userId != userId) {
            throw AccessDeniedException("Вы не являетесь владельцем этого счета")
        }

        val balance = transactionService.calculateBalance(accountId)
        val response: Map<String, Any> = mapOf(
                "balance" to (balance as Any),
                "currency" to (account.currency as Any)
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Operation(summary = "Получить список счетов", description = "Возвращает список счетов пользователя")
    @ApiResponses(
            value = [
                ApiResponse(responseCode = "200", description = "Список счетов успешно получен"),
                ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                ApiResponse(responseCode = "500", description = "Ошибка сервера")
            ]
    )
    fun getUserAccounts(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<Account>> {
        val userId = jwt.subject
        val accounts = accountRepository.findByUserId(userId)
        return ResponseEntity.ok(accounts)
    }
}
