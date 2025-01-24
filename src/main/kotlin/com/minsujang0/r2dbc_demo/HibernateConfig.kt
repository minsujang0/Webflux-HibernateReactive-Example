package com.minsujang0.r2dbc_demo

import io.r2dbc.spi.ConnectionFactory
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.persistence.PersistenceException
import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.PersistenceProviderResolverHolder
import jakarta.persistence.spi.PersistenceUnitTransactionType
import org.hibernate.bytecode.enhance.spi.EnhancementContext
import org.hibernate.bytecode.spi.ClassTransformer
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import java.net.URL
import java.util.Properties


@Configuration
class HibernateConfig {
    @Bean
    fun sessionFactory(): Mutiny.SessionFactory {
        return Persistence.createEntityManagerFactory("pu")
            .unwrap<Mutiny.SessionFactory>(Mutiny.SessionFactory::class.java)
    }

    @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}

fun createEntityManagerFactory(persistenceUnitName: String?, properties: MutableMap<*, *>?): EntityManagerFactory {
    var emf: EntityManagerFactory? = null
    val resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver()

    val providers = resolver.getPersistenceProviders()

    for (provider in providers) {
        emf = provider.createEntityManagerFactory(persistenceUnitName, properties)
        if (emf != null) {
            break
        }
    }
    if (emf == null) {
        throw PersistenceException("No Persistence provider for EntityManager named " + persistenceUnitName)
    }
    return emf
}


class ReactivePersistenceUnitDescriptor: PersistenceUnitDescriptor{
    override fun getPersistenceUnitRootUrl(): URL? {
        TODO("Not yet implemented")
    }

    override fun getName(): String? {
        return "hibernate"
    }

    override fun getProviderClassName(): String? {
       return this.javaClass.packageName
    }

    override fun isUseQuotedIdentifiers(): Boolean {
        return false
    }

    override fun isExcludeUnlistedClasses(): Boolean {
      return false
    }

    override fun getTransactionType(): PersistenceUnitTransactionType? {
       return PersistenceUnitTransactionType.RESOURCE_LOCAL
    }

    override fun getValidationMode(): ValidationMode? {
        return null
    }

    override fun getSharedCacheMode(): SharedCacheMode? {
       return null
    }

    override fun getManagedClassNames(): List<String?>? {
        return SpringApplication.
    }

    override fun getMappingFileNames(): List<String?>? {
        TODO("Not yet implemented")
    }

    override fun getJarFileUrls(): List<URL?>? {
        TODO("Not yet implemented")
    }

    override fun getNonJtaDataSource(): Any? {
        TODO("Not yet implemented")
    }

    override fun getJtaDataSource(): Any? {
        TODO("Not yet implemented")
    }

    override fun getProperties(): Properties? {
        TODO("Not yet implemented")
    }

    override fun getClassLoader(): ClassLoader? {
        TODO("Not yet implemented")
    }

    override fun getTempClassLoader(): ClassLoader? {
        TODO("Not yet implemented")
    }

    override fun pushClassTransformer(enhancementContext: EnhancementContext?) {
        TODO("Not yet implemented")
    }

    override fun getClassTransformer(): ClassTransformer? {
        TODO("Not yet implemented")
    }
}