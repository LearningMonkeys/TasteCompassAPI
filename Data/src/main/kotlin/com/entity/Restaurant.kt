package com.entity

import com.common.Status
import java.util.logging.Logger

data class Restaurant(
    private val id: String,
    private var metadata: RestaurantMetadata?,
    private var embedding: RestaurantEmbedding?,
    private var status: Status
) {
    private val logger = Logger.getLogger(TAG)

    fun toReadableString(): String {
        TODO("Not yet implemented")
    }

    fun getId(): String {
        return id
    }

    fun getMetadata(): RestaurantMetadata {
        if(metadata == null) {
            logger.severe("Metadata is not available with id=$id")
            throw IllegalStateException("Metadata is not available.")
        }
        return metadata!!
    }

    fun setMetadata(metadata: RestaurantMetadata) {
        this.metadata = metadata
    }

    fun getEmbedding(): RestaurantEmbedding {
        if(embedding == null) {
            logger.severe("Embedding is not available with id=$id")
            throw IllegalStateException("Embedding is not available.")
        }
        return embedding!!
    }

    fun setEmbedding(embedding: RestaurantEmbedding) {
        this.embedding = embedding
    }

    fun getStatus(): Status {
        return status
    }

    fun setStatus(status: Status) {
        this.status = status
    }

    companion object {
        private const val TAG = "Restaurant"

        fun createRestaurant(
            metadata: RestaurantMetadata, embedding: RestaurantEmbedding?): Restaurant {
            return Restaurant(
                id = metadata.id,
                metadata = metadata,
                embedding = embedding,
                status = metadata.status
            )
        }
    }
}