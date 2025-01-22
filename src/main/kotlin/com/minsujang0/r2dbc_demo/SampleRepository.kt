package com.minsujang0.r2dbc_demo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SampleRepository: CoroutineCrudRepository<Sample, Long> {
}