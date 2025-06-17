package com.example.controller

import com.example.dto.AccountCreationRequest
import com.example.model.Account
import com.example.repository.AccountRepository
import com.example.service.TransactionService
import com.example.controller.api.TransactionControllerApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
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
) : TransactionControllerApi {

    private val log = LoggerFactory.getLogger(TransactionController::class.java)

    @PostMapping
    override suspend fun createAccount(
            @RequestBody request: AccountCreationRequest,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Account> {
        val userId = jwt.subject
        val account = Account(userId = userId, currency = request.currency)
        val saved = accountRepository.save(account)
        return ResponseEntity.ok(saved)
    }

    @PostMapping("/{accountId}/deposit")
    override suspend fun deposit(
            @PathVariable accountId: Long,
            @RequestBody amount: BigDecimal,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        transactionService.deposit(accountId, amount, jwt.subject)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{accountId}/withdraw")
    override suspend fun withdraw(
            @PathVariable accountId: Long,
            @RequestBody amount: BigDecimal,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        transactionService.withdraw(accountId, amount, jwt.subject)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{accountId}/balance")
    override suspend fun getBalance(
            @PathVariable accountId: Long,
            @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Map<String, Any>> {
        val userId = jwt.subject
        val account = accountRepository.findById(accountId) ?: throw NoSuchElementException("Счет не найден")

        if (account.userId != userId) {
            throw AccessDeniedException("Вы не являетесь владельцем этого счета")
        }

        val balance = transactionService.calculateBalance(accountId)
        return ResponseEntity.ok(mapOf("balance" to balance, "currency" to account.currency))
    }

    @GetMapping
    override suspend fun getUserAccounts(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<Account>> {
        val userId = jwt.subject
        log.debug("Acquiring user $userId accounts")
        val accounts = accountRepository
                .findByUserId(userId)
                .asFlow()
                .toList()
        return ResponseEntity.ok(accounts)
    }
}
