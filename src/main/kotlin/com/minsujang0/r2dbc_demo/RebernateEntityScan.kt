package com.minsujang0.r2dbc_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class RebernateEntityScan(vararg val value: String)

fun getRebernateEntityScanValue(context: ApplicationContext): List<String> {
    val mainClass = context.getBeansWithAnnotation(SpringBootApplication::class.java).values.first().javaClass
    val rebernateEntityScan = mainClass.getAnnotation(RebernateEntityScan::class.java).value
    return rebernateEntityScan.toList()
}