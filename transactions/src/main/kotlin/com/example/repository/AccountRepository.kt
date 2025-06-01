package com.example.repository

import com.example.model.Account
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import java.util.*

interface AccountRepository : JpaRepository<Account, Long> {
    fun findByUserId(userId: String): List<Account>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :accountId")
    @QueryHints(
            QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")
    )
    fun findByIdForUpdate(@Param("accountId") accountId: Long): Optional<Account>
}
