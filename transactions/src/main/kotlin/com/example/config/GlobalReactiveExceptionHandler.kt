package com.example.config

import com.example.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono


@Component
@Order(-2)
class GlobalReactiveExceptionHandler(
        serverCodecConfigurer: ServerCodecConfigurer,
        applicationContext: ApplicationContext,
        webProperties: WebProperties
) : AbstractErrorWebExceptionHandler(
        DefaultErrorAttributes(),
        webProperties.resources,
        applicationContext
) {
    private val log = LoggerFactory.getLogger(GlobalReactiveExceptionHandler::class.java)

    init {
        this.setMessageWriters(serverCodecConfigurer.writers)
        this.setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all(), ::handleErrorResponse)
    }

    private fun handleErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val error = getError(request)
        val status: HttpStatus
        val message: String

        when (error) {
            is NoSuchElementException -> {
                status = HttpStatus.NOT_IMPLEMENTED
                message = error.message ?: "Account not found"
                log.error("❌ Account not found: $message")
            }
            is AccessDeniedException -> {
                status = HttpStatus.FAILED_DEPENDENCY
                message = error.message ?: "Access denied"
                log.error("🚫 Access denied: $message")
            }
            is IllegalArgumentException -> {
                status = HttpStatus.UNPROCESSABLE_ENTITY
                message = error.message ?: "Invalid argument"
                log.error("⚠️ Invalid operation: $message")
            }
            is IllegalStateException -> {
                status = HttpStatus.UNPROCESSABLE_ENTITY
                message = error.message ?: "Invalid state"
                log.error("⚠️ Invalid state: $message")
            }
            else -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR
                message = error.message ?: "Unexpected error"
                log.error("💥 Unexpected error: $message", error)
            }
        }

        val body = ErrorResponse(status.value(), status.reasonPhrase, message)
        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
    }
}

