package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Transactions

fun main(args: Array<String>) {
    runApplication<Transactions>(*args)
}
