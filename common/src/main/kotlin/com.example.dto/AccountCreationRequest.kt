package com.example.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountCreationRequest(@JsonProperty("currency") val currency: String)
