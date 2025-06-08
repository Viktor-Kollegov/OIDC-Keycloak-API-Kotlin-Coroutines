package com.example.repository

import com.example.model.Transaction
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono
import java.math.BigDecimal

interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
    @Query("SELECT id, account_id, amount FROM transaction WHERE account_id = :accountId")
    fun findByAccountId(accountId: Long): Flow<Transaction>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transaction WHERE account_id = :accountId")
    fun sumAmountsByAccountId(accountId: Long): Mono<BigDecimal>
}
