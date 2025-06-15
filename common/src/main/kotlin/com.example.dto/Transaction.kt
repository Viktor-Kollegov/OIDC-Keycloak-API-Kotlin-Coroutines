package com.example.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.ZonedDateTime

@Table("transaction")
data class Transaction(
        @Id
        val id: Long? = null,
        val accountId: Long,
        val amount: BigDecimal,
        val timestamp: ZonedDateTime = ZonedDateTime.now()
)
