package com.example.repository

import com.example.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface TransactionRepository : JpaRepository<Transaction, Long> {

    fun findByAccountId(accountId: Long): List<Transaction>

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.id = :accountId")
    fun sumAmountsByAccountId(@Param("accountId") accountId: Long): BigDecimal
}
