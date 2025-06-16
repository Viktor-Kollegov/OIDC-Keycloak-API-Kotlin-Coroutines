package com.example.service

import java.math.BigDecimal

interface TransactionService {
    suspend fun deposit(accountId: Long, amount: BigDecimal, userId: String)
    suspend fun withdraw(accountId: Long, amount: BigDecimal, userId: String)
    suspend fun calculateBalance(accountId: Long): BigDecimal
}
