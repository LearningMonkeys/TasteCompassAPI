package com.service.repository.mongodb

import com.entity.RestaurantMetadata
import org.springframework.stereotype.Repository as SpringRepository

@SpringRepository
class RestaurantMetadataRepository(

): MetadataRepository<RestaurantMetadata> {
    override fun insert(entityList: List<RestaurantMetadata>) {
        TODO("Not yet implemented")
    }

    override fun update(entityList: List<RestaurantMetadata>) {
        TODO("Not yet implemented")
    }

    override fun delete(idList: List<String>) {
        TODO("Not yet implemented")
    }

    override fun get(idList: List<String>): List<RestaurantMetadata> {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<RestaurantMetadata> {
        TODO("Not yet implemented")
    }
}