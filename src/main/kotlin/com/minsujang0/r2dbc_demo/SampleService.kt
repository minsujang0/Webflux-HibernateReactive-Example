package com.minsujang0.r2dbc_demo

import io.smallrye.mutiny.coroutines.asUni
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.transaction.Transactional
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class SampleService(
    private val sampleRepository: SampleRepository, private val sampleDslRepository: SampleDslRepository,
    private val sessionFactory: SessionFactory,
) {
    @Transactional
    suspend fun createSample(name: String): Sample = coroutineScope {
        sessionFactory.withTransaction(Function {
            async {
                val sample = sampleRepository.save(Sample(name = name))
                sample.name = "Created, ${sample.name}"
                sample
            }.asUni()
        }).awaitSuspending()
    }

    suspend fun getSample(id: Long): Sample = coroutineScope {
        sessionFactory.withTransaction(Function {
            async {
                val sample = sampleDslRepository.getSampleById(id)
                sample.name = "Hello, ${sample.name}"
                sample
            }.asUni()
        }).awaitSuspending()
    }
}