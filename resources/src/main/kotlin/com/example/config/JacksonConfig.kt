package com.example.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

@Configuration
class JacksonConfig : WebFluxConfigurer {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(KotlinModule.Builder().build())
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        val mapper = objectMapper()
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper))
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(mapper))
    }
}