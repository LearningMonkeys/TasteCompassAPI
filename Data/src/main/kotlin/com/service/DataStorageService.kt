package com.service

import kotlinx.coroutines.flow.Flow

interface DataStorageService<T> {
    fun search(fieldName: String, topK: Int, vectorList: List<List<Float>>): List<List<T>>
    fun insert(entityList: List<T>)
    fun update(entityList: List<T>)
    fun delete(idList: List<String>)
    fun get(idList: List<String>): List<T>
    fun getAll(): List<T>
    fun getAsFlow(): Flow<T>
}