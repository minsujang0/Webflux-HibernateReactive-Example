package com.minsujang0.r2dbc_demo

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.reactive.extension.createQuery
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.stereotype.Component

@Component
class SampleDslRepository(private val sessionFactory: Mutiny.SessionFactory) {
    suspend fun getSampleById(id: Long): Sample {
        val query = jpql {
            select(entity(Sample::class))
                .from(entity(Sample::class))
                .where(path(Sample::id).eq(id))
        }

        return sessionFactory.withSession {
                it.createQuery(query, JpqlRenderContext()).singleResult
            }.awaitSuspending()
    }
}