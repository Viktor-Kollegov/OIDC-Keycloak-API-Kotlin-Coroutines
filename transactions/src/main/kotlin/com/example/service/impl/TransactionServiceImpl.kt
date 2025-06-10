package com.example.service.impl

import com.example.model.Transaction
import com.example.repository.AccountRepository
import com.example.repository.TransactionRepository
import com.example.service.TransactionService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.reactive.executeAndAwait

@Service
class TransactionServiceImpl(
        private val transactionRepository: TransactionRepository,
        private val accountRepository: AccountRepository,
        private val transactionalOperator: TransactionalOperator
) : TransactionService {

    private val locks = ConcurrentHashMap<Long, Mutex>()

    override suspend fun deposit(accountId: Long, amount: BigDecimal, userId: String) {
        val lock = locks.computeIfAbsent(accountId) { Mutex() }
        lock.withLock {
            transactionalOperator.executeAndAwait {
                val account = accountRepository.findByIdForUpdate(accountId).awaitFirstOrNull()
                        ?: throw NoSuchElementException("Account not found")

                if (account.userId != userId)
                    throw AccessDeniedException("You are not the owner of this account.")

                if (amount <= BigDecimal.ZERO)
                    throw IllegalArgumentException("The replenishment amount must be positive")

                transactionRepository.save(Transaction(accountId = accountId, amount = amount))
            }
        }
    }

    override suspend fun withdraw(accountId: Long, amount: BigDecimal, userId: String) {
        val lock = locks.computeIfAbsent(accountId) { Mutex() }
        lock.withLock {
            transactionalOperator.executeAndAwait {
                val account = accountRepository.findByIdForUpdate(accountId).awaitFirstOrNull()
                        ?: throw NoSuchElementException("Account not found")

                if (account.userId != userId)
                    throw AccessDeniedException("You are not the owner of this account.")
                if (amount <= BigDecimal.ZERO)
                    throw IllegalArgumentException("The withdrawal amount must be positive.")

                val balance = transactionRepository.sumAmountsByAccountId(accountId).awaitFirst()
                if (balance < amount)
                    throw IllegalStateException("Insufficient funds in the account")

                transactionRepository.save(Transaction(accountId = accountId, amount = amount.negate()))
            }
        }
    }

    override suspend fun calculateBalance(accountId: Long): BigDecimal {
        return transactionRepository.sumAmountsByAccountId(accountId).awaitFirst()
    }
}
