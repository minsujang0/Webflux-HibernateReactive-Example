package com.minsujang0.r2dbc_demo

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "sample")
class Sample(
) {
    var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }

    @Id
    val id: Long = 0
}