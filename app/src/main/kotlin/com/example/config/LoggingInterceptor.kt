package com.example.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader

class LoggingInterceptor : ClientHttpRequestInterceptor {
    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        log.debug("Request: {} {}", request.method, request.uri)
        log.debug("Request Headers: {}", request.headers)
        log.debug("Request Body: {}", String(body))
        val response = execution.execute(request, body)
        val bufferedResponse = BufferedClientHttpResponse(response)
        log.debug("Response: {} {}", response.statusCode, response.statusText)
        log.debug("Response Headers: {}", response.headers)
        bufferedResponse.body.let {
            val reader = BufferedReader(InputStreamReader(it))
            val responseBody = reader.readText()
            log.debug("Response Body: {}", responseBody)
        }
        return bufferedResponse
    }

    class BufferedClientHttpResponse(private val response: ClientHttpResponse) : ClientHttpResponse {
        private var body: ByteArray? = null

        override fun getStatusCode(): HttpStatusCode {
            return response.statusCode
        }

        override fun getStatusText(): String {
            return response.statusText
        }

        override fun getHeaders(): HttpHeaders {
            return response.headers
        }

        override fun getBody(): InputStream {
            if (body == null) {
                body = StreamUtils.copyToByteArray(response.body)
            }
            return ByteArrayInputStream(body)
        }

        override fun close() {
            response.close()
        }
    }
}