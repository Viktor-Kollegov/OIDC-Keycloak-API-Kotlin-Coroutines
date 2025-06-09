package com.example.repository

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryTest {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Test
    fun `should return all accounts by userId`() = runTest {
        val accounts = accountRepository.findByUserId("test-user").asFlow().toList()
        assertThat(accounts).hasSize(2)
        assertThat(accounts.map { it.currency }).containsExactlyInAnyOrder("USD", "EUR")
    }
}