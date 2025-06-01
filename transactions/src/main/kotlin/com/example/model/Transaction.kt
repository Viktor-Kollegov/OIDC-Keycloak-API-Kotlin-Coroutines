package com.example.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Transaction(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne
        @JoinColumn(name = "account_id")
        var account: Account? = null,

        var amount: BigDecimal? = null,
        var timestamp: LocalDateTime? = null
)
