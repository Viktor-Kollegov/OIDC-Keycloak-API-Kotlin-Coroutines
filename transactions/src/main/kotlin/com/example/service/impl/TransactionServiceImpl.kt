package com.example.service.impl

import com.example.model.Transaction
import com.example.repository.AccountRepository
import com.example.repository.TransactionRepository
import com.example.service.TransactionService
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionServiceImpl(
        private val transactionRepository: TransactionRepository,
        private val accountRepository: AccountRepository
) : TransactionService {

    @Transactional
    override fun deposit(accountId: Long, amount: BigDecimal, userId: String) {
        val account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow { EntityNotFoundException("Счет не найден") }

        if (account.userId != userId) {
            throw AccessDeniedException("Вы не являетесь владельцем этого счета")
        }
        if (amount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Сумма пополнения должна быть положительной")
        }

        val transaction = Transaction().apply {
            this.account = account
            this.amount = amount
            this.timestamp = LocalDateTime.now()
        }
        transactionRepository.save(transaction)
    }

    @Transactional
    override fun withdraw(accountId: Long, amount: BigDecimal, userId: String) {
        val account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow { EntityNotFoundException("Счет не найден") }

        if (account.userId != userId) {
            throw AccessDeniedException("Вы не являетесь владельцем этого счета")
        }
        if (amount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Сумма снятия должна быть положительной")
        }

        val balance = calculateBalance(accountId)
        if (balance < amount) {
            throw IllegalStateException("Недостаточно средств на счете")
        }

        val transaction = Transaction().apply {
            this.account = account
            this.amount = amount.negate()
            this.timestamp = LocalDateTime.now()
        }
        transactionRepository.save(transaction)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    override fun calculateBalance(accountId: Long): BigDecimal {
        return transactionRepository.sumAmountsByAccountId(accountId)
    }
}
