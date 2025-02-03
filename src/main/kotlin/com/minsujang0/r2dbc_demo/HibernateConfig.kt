package com.minsujang0.r2dbc_demo

import jakarta.persistence.Entity
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.PersistenceUnitTransactionType
import org.hibernate.bytecode.enhance.spi.EnhancementContext
import org.hibernate.bytecode.spi.ClassTransformer
import org.hibernate.cfg.AvailableSettings
import org.hibernate.cfg.JdbcSettings
import org.hibernate.cfg.SchemaToolingSettings
import org.hibernate.dialect.H2Dialect
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.pool.impl.ExternalSqlClientPool
import org.hibernate.reactive.provider.Settings
import org.hibernate.reactive.provider.impl.ReactiveEntityManagerFactoryBuilder
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.type.filter.AnnotationTypeFilter
import java.net.URL
import java.util.*
import javax.sql.DataSource
import kotlin.Any


@Configuration
class HibernateConfig {
    @Bean
    fun sessionFactory(
        entityManagerFactory: EntityManagerFactory,
    ): Mutiny.SessionFactory {
        return entityManagerFactory
            .unwrap<Mutiny.SessionFactory>(Mutiny.SessionFactory::class.java)
    }

    @Bean
    fun entityManagerFactory(
        applicationContext: ApplicationContext,
        externalSqlClient: ExternalSqlClientPool,
        dataSource: DataSource,
    ): EntityManagerFactory {
        return ReactiveEntityManagerFactoryBuilder(
            ReactivePersistenceUnitDescriptor(applicationContext),
//            mapOf<Any, Any>(
//                JdbcSettings.JAKARTA_JDBC_URL to "jdbc:postgresql://localhost:5432/quarkus",
//                JdbcSettings.JAKARTA_JDBC_USER to "postgres",
//                JdbcSettings.JAKARTA_JDBC_PASSWORD to "postgres",
//                JdbcSettings.POOL_SIZE to "10",
//                SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION to "update"
//            ),
            mapOf<Any, Any>(
                Settings.SQL_CLIENT_POOL to externalSqlClient,
                Settings.DIALECT to H2Dialect::class.java.name,
                SchemaToolingSettings.HBM2DDL_AUTO to "update",
                AvailableSettings.SCHEMA_MANAGEMENT_TOOL to HibernateSchemaManagementTool::class.java.name,
//                AvailableSettings.CONNECTION_PROVIDER to "org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl", // Automatically set as DataSource is provided
                JdbcSettings.JAKARTA_JTA_DATASOURCE to dataSource,
            )
        ).build()
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
