package com.example.config

import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("!test")
class LiquibaseConfig {

    @Bean
    fun dataSource(): HikariDataSource {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = "jdbc:postgresql://postgres:5432/resourcedb"
        dataSource.username = "postgres"
        dataSource.password = "postgres"
        dataSource.driverClassName = "org.postgresql.Driver"
        return dataSource
    }

    @Bean
    fun liquibase(dataSource: DataSource): SpringLiquibase {
        val liquibase = SpringLiquibase()
        liquibase.dataSource = dataSource
        liquibase.changeLog = "classpath:db/changelog/db.changelog-master.yaml"
        return liquibase
    }
}