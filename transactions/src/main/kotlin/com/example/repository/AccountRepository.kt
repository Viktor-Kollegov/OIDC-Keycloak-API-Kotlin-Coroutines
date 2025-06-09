package com.example.repository

import com.example.model.Account
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccountRepository : CoroutineCrudRepository<Account, Long> {
    @Query("SELECT id, user_id, currency FROM account WHERE user_id = :userId")
    fun findByUserId(userId: String): Flux<Account>

    @Query("SELECT id, user_id, currency FROM account WHERE id = :accountId")
    fun findByIdForUpdate(accountId: Long): Mono<Account>
}