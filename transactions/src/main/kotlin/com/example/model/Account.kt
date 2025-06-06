package com.example.model

import org.springframework.data.annotation.Id

data class Account(
        @Id
        val id: Long? = null,
        val userId: String,
        val currency: String
)
