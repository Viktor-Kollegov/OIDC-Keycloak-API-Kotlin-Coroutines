package com.example.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("account")
data class Account(
        @Id
        val id: Long? = null,
        val userId: String,
        val currency: String
)
