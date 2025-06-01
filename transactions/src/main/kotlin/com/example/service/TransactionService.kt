package com.example.service

import java.math.BigDecimal

interface TransactionService {
    fun deposit(accountId: Long, amount: BigDecimal, userId: String)
    fun withdraw(accountId: Long, amount: BigDecimal, userId: String)
    fun calculateBalance(accountId: Long): BigDecimal
}
