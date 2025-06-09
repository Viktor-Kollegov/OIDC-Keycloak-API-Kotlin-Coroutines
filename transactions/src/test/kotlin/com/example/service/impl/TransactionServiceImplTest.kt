package com.example.service.impl

import com.example.model.Account
import com.example.repository.AccountRepository
import com.example.repository.TransactionRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.reactive.TransactionalOperator
import java.math.BigDecimal

@DataR2dbcTest
@Import(TransactionServiceImpl::class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest(
        @Autowired val transactionRepository: TransactionRepository,
        @Autowired val accountRepository: AccountRepository,
        @Autowired val transactionalOperator: TransactionalOperator
) {

    private val service by lazy {
        TransactionServiceImpl(transactionRepository, accountRepository, transactionalOperator)
    }

    @Test
    fun `deposit should create transaction`() = runTest {
        val account = accountRepository.save(Account(userId = "user", currency = "RUB"))
        service.deposit(account.id!!, BigDecimal("0.02"), "user")
        val transactions = transactionRepository.findByAccountId(account.id!!).collectList().block()
        assert(transactions!!.any { it.amount == BigDecimal("0.02") })
    }
}
