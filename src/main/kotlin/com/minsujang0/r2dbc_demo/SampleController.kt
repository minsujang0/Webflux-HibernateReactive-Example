package com.minsujang0.r2dbc_demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(private val sampleService: SampleService) {
    @GetMapping
    suspend fun getSample(@RequestParam id: Long): Sample {
        return sampleService.getSample(id)
    }

    data class CreateSampleRequest(val name: String)

    @PostMapping
    suspend fun createSample(@RequestBody request: CreateSampleRequest): Sample {
        return sampleService.createSample(request.name)
    }
}