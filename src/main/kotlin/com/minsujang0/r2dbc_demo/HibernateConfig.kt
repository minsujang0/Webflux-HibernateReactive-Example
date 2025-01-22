package com.minsujang0.r2dbc_demo

import io.r2dbc.spi.ConnectionFactory
import jakarta.persistence.Persistence
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager


@Configuration
class HibernateConfig {
    @Bean
    fun sessionFactory(): Mutiny.SessionFactory {
        return Persistence.createEntityManagerFactory("pu")
            .unwrap<Mutiny.SessionFactory>(Mutiny.SessionFactory::class.java)
    }

    @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}