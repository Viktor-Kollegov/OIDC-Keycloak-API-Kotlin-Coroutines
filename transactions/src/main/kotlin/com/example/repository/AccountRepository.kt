package com.example.repository

import com.example.model.Account
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono

interface AccountRepository : CoroutineCrudRepository<Account, Long> {
    fun findByUserId(userId: String): Flow<Account>

    @Query("SELECT id, user_id, currency FROM account WHERE id = :accountId")
    fun findByIdForUpdate(accountId: Long): Mono<Account>
}