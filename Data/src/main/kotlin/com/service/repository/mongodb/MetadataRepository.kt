package com.service.repository.mongodb

interface MetadataRepository<T> {
    fun insert(entityList: List<T>)
    fun update(entityList: List<T>)
    fun delete(idList: List<String>)
    fun get(idList: List<String>): List<T>
    fun getAll(): List<T>
}