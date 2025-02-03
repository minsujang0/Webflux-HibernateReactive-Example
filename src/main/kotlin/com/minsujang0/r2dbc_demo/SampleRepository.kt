package com.minsujang0.r2dbc_demo

import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.stereotype.Component

@Component
class SampleRepository(
    sessionFactory: Mutiny.SessionFactory
) : JdslRepository<Sample, Long>(
    sessionFactory, Sample::class
)