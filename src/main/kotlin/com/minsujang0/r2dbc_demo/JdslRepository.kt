package com.minsujang0.r2dbc_demo

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.delete.DeleteQuery
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQueries
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.querymodel.jpql.update.UpdateQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.reactive.extension.createMutationQuery
import com.linecorp.kotlinjdsl.support.hibernate.reactive.extension.createQuery
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.hibernate.reactive.mutiny.Mutiny
import kotlin.reflect.KClass

abstract class JdslRepository<T : Any, ID>(
    private val sessionFactory: Mutiny.SessionFactory,
    private val entityClass: KClass<T>,
) {
    suspend fun count(): Long {
        return jpql {
            select(
                count(value(1L))
            ).from(entity(entityClass))
        }.awaitSuspending() ?: 0
    }

    suspend fun delete(entity: T) {
        sessionFactory.withSession { session ->
            session.remove(entity)
        }.awaitSuspending()
    }

    suspend fun deleteAll() {
        val entities = findAll()
        sessionFactory.withSession { session ->
            session.removeAll(entities)
        }.awaitSuspending()
    }

    suspend fun deleteAll(entities: Iterable<T>) {
        sessionFactory.withSession { session ->
            session.removeAll(entities)
        }.awaitSuspending()
    }

    suspend fun <S : T> deleteAll(entityStream: Flow<S>) {
        sessionFactory.withSession { session ->
            session.removeAll(entityStream)
        }.awaitSuspending()
    }

    suspend fun deleteAllById(vararg ids: ID) {
        val entities = findAllById(*ids)
        sessionFactory.withSession { session ->
            session.removeAll(entities)
        }.awaitSuspending()
    }

    suspend fun deleteById(id: ID) {
        val entity = findById(id) ?: return
        sessionFactory.withSession { session ->
            session.remove(entity)
        }.awaitSuspending()
    }

    suspend fun existsById(id: ID): Boolean {
        return findById(id) != null
    }

    val entity = object : Entity<T> {
        override val alias: String
            get() = entityClass.simpleName!!
    }

    suspend fun findAll(): List<T> {
        return SelectQueries.selectQuery(
            entityClass,
            false,
            listOf(entity),
            listOf(entity),
        ).awaitSuspendingList()
    }

    suspend fun findAllById(vararg ids: ID): List<T> {
        return sessionFactory.withSession { session ->
            session.find(entityClass.java, *ids)
        }.awaitSuspending()
    }

    suspend fun findById(id: ID): T? {
        return sessionFactory.withSession { session ->
            session.find(entityClass.java, id)
        }.awaitSuspending()
    }

    suspend fun <S : T> save(entity: S): S {
        sessionFactory.withSession { session ->
            session.persist(entity)
        }.awaitSuspending()
        return entity
    }

    suspend fun <S : T> saveAll(entities: Iterable<S>): Flow<S> {
        return flow {
            entities.forEach { entity ->
                emit(save(entity))
            }
        }
    }

    fun <S : T> saveAll(entityStream: Flow<S>): Flow<S> {
        return entityStream.map { entity ->
            save(entity)
        }
    }

    fun <T : Any> SelectQuery<T>.uni(): Uni<T?> {
        return sessionFactory.withSession { session ->
            session.createQuery(this, JpqlRenderContext()).singleResult
        }
    }

    fun <T : Any> SelectQuery<T>.uniList(): Uni<List<T>> {
        return sessionFactory.withSession { session ->
            session.createQuery(this, JpqlRenderContext()).resultList
        }
    }

    suspend fun <T : Any> SelectQuery<T>.awaitSuspending(): T? {
        return sessionFactory.withSession { session ->
            session.createQuery(this, JpqlRenderContext()).singleResult
        }.awaitSuspending()
    }

    suspend fun <T : Any> SelectQuery<T>.awaitSuspendingList(): List<T> {
        return sessionFactory.withSession { session ->
            session.createQuery(this, JpqlRenderContext()).resultList
        }.awaitSuspending()
    }

    fun <T : Any> DeleteQuery<T>.uni(): Uni<Int> {
        return sessionFactory.withSession { session ->
            session.createMutationQuery(this, JpqlRenderContext()).executeUpdate()
        }
    }

    suspend fun <T : Any> DeleteQuery<T>.awaitSuspending(): Int {
        return sessionFactory.withSession { session ->
            session.createMutationQuery(this, JpqlRenderContext()).executeUpdate()
        }.awaitSuspending()
    }

    fun <T : Any> UpdateQuery<T>.uni(): Uni<Int> {
        return sessionFactory.withSession { session ->
            session.createMutationQuery(this, JpqlRenderContext()).executeUpdate()
        }
    }

    suspend fun <T : Any> UpdateQuery<T>.awaitSuspending(): Int {
        return sessionFactory.withSession { session ->
            session.createMutationQuery(this, JpqlRenderContext()).executeUpdate()
        }.awaitSuspending()
    }
}