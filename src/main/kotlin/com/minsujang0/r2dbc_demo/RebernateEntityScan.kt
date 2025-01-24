package com.minsujang0.r2dbc_demo

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RebernateEntityScan(vararg val value: String)
