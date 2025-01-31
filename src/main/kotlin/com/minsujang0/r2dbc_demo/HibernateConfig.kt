package com.minsujang0.r2dbc_demo

import io.r2dbc.spi.ConnectionFactory
import jakarta.persistence.Entity
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.PersistenceUnitTransactionType
import org.hibernate.bytecode.enhance.spi.EnhancementContext
import org.hibernate.bytecode.spi.ClassTransformer
import org.hibernate.cfg.JdbcSettings
import org.hibernate.cfg.SchemaToolingSettings
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.provider.impl.ReactiveEntityManagerFactoryBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import java.net.URL
import java.util.*


@Configuration
class HibernateConfig {
    @Bean
    fun sessionFactory(
        entityManagerFactory: EntityManagerFactory
    ): Mutiny.SessionFactory {
        return entityManagerFactory
            .unwrap<Mutiny.SessionFactory>(Mutiny.SessionFactory::class.java)
    }

    @Bean
    fun entityManagerFactory(applicationContext: ApplicationContext): EntityManagerFactory {
        return ReactiveEntityManagerFactoryBuilder(
            ReactivePersistenceUnitDescriptor(applicationContext),
            mapOf<Any, Any>(
                JdbcSettings.JAKARTA_JDBC_URL to "jdbc:postgresql://localhost:5432/quarkus",
                JdbcSettings.JAKARTA_JDBC_USER to "postgres",
                JdbcSettings.JAKARTA_JDBC_PASSWORD to "postgres",
                JdbcSettings.POOL_SIZE to "10",
                SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION to "update"
            )
        ).build()
    }

    @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}

class ReactivePersistenceUnitDescriptor(val applicationContext: ApplicationContext) : PersistenceUnitDescriptor {
    override fun getPersistenceUnitRootUrl(): URL? = null

    override fun getName(): String? = "default"

    override fun getProviderClassName(): String? = this.javaClass.packageName

    override fun getTransactionType(): PersistenceUnitTransactionType? = PersistenceUnitTransactionType.RESOURCE_LOCAL

    override fun getManagedClassNames(): List<String?>? {
        val scanValues = getRebernateEntityScanValue(applicationContext)
        val classNames = scanValues.flatMap { packageName ->
            // create a scanner for the entity classes
            val provider = ClassPathScanningCandidateComponentProvider(false).apply {
                addIncludeFilter(AnnotationTypeFilter(Entity::class.java))
                resourceLoader = DefaultResourceLoader()
            }

            // scan the package for entities
            provider.findCandidateComponents(packageName).mapNotNull { candidate ->
                // check if the class is loadable
                try {
                    Class.forName(candidate.beanClassName)
                    candidate.beanClassName
                } catch (e: ClassNotFoundException) {
                    null
                }
            }
        }
        return classNames
    }

    override fun isUseQuotedIdentifiers(): Boolean = false

    override fun isExcludeUnlistedClasses(): Boolean = true

    override fun getValidationMode(): ValidationMode? = null
    override fun getSharedCacheMode(): SharedCacheMode? = null
    override fun getMappingFileNames(): List<String?>? = emptyList()
    override fun getJarFileUrls(): List<URL?>? = emptyList()
    override fun getJtaDataSource(): Any? = null
    override fun getNonJtaDataSource(): Any? = null
    override fun getProperties(): Properties? = null
    override fun getClassLoader(): ClassLoader? = null
    override fun getTempClassLoader(): ClassLoader? = null
    override fun pushClassTransformer(enhancementContext: EnhancementContext?) {}
    override fun getClassTransformer(): ClassTransformer? = null
}
