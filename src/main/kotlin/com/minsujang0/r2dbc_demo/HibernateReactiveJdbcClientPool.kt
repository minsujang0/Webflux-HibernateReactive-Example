package com.minsujang0.r2dbc_demo

import io.vertx.core.Vertx
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.PoolOptions
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper
import org.hibernate.engine.jdbc.spi.SqlStatementLogger
import org.hibernate.reactive.pool.impl.ExternalSqlClientPool
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class HibernateReactiveJdbcClientPool {
    @Bean
    fun h2ExternalSqlClientPool(
        h2Pool: JDBCPool,
    ): ExternalSqlClientPool {
        return ExternalSqlClientPool(
            h2Pool,
            SqlStatementLogger(),
            SqlExceptionHelper(true)
        )
    }

    @Bean
    fun h2Pool(
        dataSource: DataSource,
    ): JDBCPool {
        return JDBCPool.pool(
            Vertx.currentContext()?.owner() ?: Vertx.vertx(),
            dataSource,
        )
    }

    @Bean
    fun dataSource(): DataSource {
        return HikariCPDataSourceProvider()
            .getDataSource(
                HikariCPDataSourceProvider().toJson(
                    JDBCConnectOptions()
                        .setJdbcUrl("jdbc:h2:mem:aaa;MODE=MYSQL;INIT=CREATE SCHEMA IF NOT EXISTS aaa;")
                        .setUser("sa")
                        .setPassword(""),
                    PoolOptions()
                        .setMaxSize(5)
                        .setName("h2Pool"),
                ).put("driverClassName", "org.h2.Driver")
            )
    }
}