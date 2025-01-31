package com.minsujang0.r2dbc_demo

import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class SampleRepository(
    sessionFactory: Mutiny.SessionFactory
) : JdslRepository<Sample, Long>(
    sessionFactory, Sample::class
)