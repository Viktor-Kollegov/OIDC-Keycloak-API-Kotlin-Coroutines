package com.example.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.transaction.reactive.TransactionalOperator


@Configuration
class R2dbcConfig {
    @Bean
    fun r2dbcTransactionManager(connectionFactory: ConnectionFactory): R2dbcTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    fun transactionalOperator(r2dbcTransactionManager: R2dbcTransactionManager): TransactionalOperator {
        return TransactionalOperator.create(r2dbcTransactionManager)
    }

    @Bean
    fun databaseClient(connectionFactory: ConnectionFactory): DatabaseClient {
        return DatabaseClient.create(TransactionAwareConnectionFactoryProxy(connectionFactory))
    }

    @Bean
    fun r2dbcEntityTemplate(connectionFactory: ConnectionFactory): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory)
    }
}