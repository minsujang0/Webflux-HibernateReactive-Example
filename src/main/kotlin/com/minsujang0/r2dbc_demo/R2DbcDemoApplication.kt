package com.minsujang0.r2dbc_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = ["com.minsujang0"])
class R2DbcDemoApplication

fun main(args: Array<String>) {
    runApplication<R2DbcDemoApplication>(*args)
}
