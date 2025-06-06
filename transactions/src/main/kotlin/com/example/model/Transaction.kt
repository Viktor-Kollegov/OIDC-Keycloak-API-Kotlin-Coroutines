package com.example.model

import org.springframework.data.annotation.Id
import java.math.BigDecimal

data class Transaction(
        @Id
        val id: Long? = null,
        val accountId: Long,
        val amount: BigDecimal
)
