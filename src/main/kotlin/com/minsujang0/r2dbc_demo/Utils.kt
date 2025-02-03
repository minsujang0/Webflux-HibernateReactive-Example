package com.minsujang0.r2dbc_demo

import io.smallrye.mutiny.coroutines.asUni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.hibernate.reactive.mutiny.Mutiny
import java.util.function.Function

suspend fun <T> tx(sessionFactory: Mutiny.SessionFactory, block: suspend CoroutineScope.() -> T): T {
    return coroutineScope {
        sessionFactory.withTransaction(Function {
            async(block = block).asUni()
        }).awaitSuspending()
    }
}
