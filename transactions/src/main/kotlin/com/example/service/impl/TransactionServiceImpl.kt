package com.example.service.impl

import com.example.model.Transaction
import com.example.repository.AccountRepository
import com.example.repository.TransactionRepository
import com.example.service.TransactionService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.reactive.awaitFirst
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
                val account = accountRepository.findByIdForUpdate(accountId)
                        .switchIfEmpty(Mono.error(NoSuchElementException("Счет не найден")))
                        .awaitFirst()

                if (account.userId != userId)
                    throw AccessDeniedException("Вы не являетесь владельцем этого счета")

                if (amount <= BigDecimal.ZERO)
                    throw IllegalArgumentException("Сумма пополнения должна быть положительной")

                transactionRepository.save(Transaction(accountId = accountId, amount = amount))
            }
        }
    }

    override suspend fun withdraw(accountId: Long, amount: BigDecimal, userId: String) {
        val lock = locks.computeIfAbsent(accountId) { Mutex() }
        lock.withLock {
            transactionalOperator.executeAndAwait {
                val account = accountRepository.findByIdForUpdate(accountId)
                        .switchIfEmpty(Mono.error(NoSuchElementException("Счет не найден")))
                        .awaitFirst()

                if (account.userId != userId)
                    throw AccessDeniedException("Вы не являетесь владельцем этого счета")
                if (amount <= BigDecimal.ZERO)
                    throw IllegalArgumentException("Сумма снятия должна быть положительной")

                val balance = transactionRepository.sumAmountsByAccountId(accountId).awaitFirst()
                if (balance < amount)
                    throw IllegalStateException("Недостаточно средств на счете")

                transactionRepository.save(Transaction(accountId = accountId, amount = amount.negate()))
            }
        }
    }

    override suspend fun calculateBalance(accountId: Long): BigDecimal {
        return transactionRepository.sumAmountsByAccountId(accountId).awaitFirst()
    }
}
