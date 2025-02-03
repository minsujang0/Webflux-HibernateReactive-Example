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
    suspend fun createSample(name: String): Sample = tx(sessionFactory) {
        val sample = sampleRepository.save(Sample(name = name))
        sample.name = "Created, ${sample.name}"
        sample
    }

    suspend fun getSample(id: Long): Sample = tx(sessionFactory) {
        val sample = sampleRepository.findById(id)!!
        sample.name = "Hello, ${sample.name}"
        sample
    }
}