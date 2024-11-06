package com.service.repository

import io.milvus.v2.service.vector.request.data.BaseVector
import kotlinx.coroutines.flow.Flow

interface Repository<Entity> {
    fun insert(entities: List<Entity>)
    fun upsert(entities: List<Entity>)
    fun delete(ids: List<Long>)
    fun search(data: List<List<Float>>, topK: Int): List<List<Entity>>
    fun get(ids: List<Long>): List<Entity>
    fun getAll(): List<Entity>
    fun getAsFlow(): Flow<Entity>
}