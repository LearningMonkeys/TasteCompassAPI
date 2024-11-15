package com.service

import com.common.Status
import com.entity.Restaurant
import com.entity.RestaurantEmbedding
import com.entity.RestaurantMetadata
import com.service.repository.milvus.EmbeddingRepository
import com.service.repository.mongodb.MetadataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class RestaurantService(
    private val metadataRepository: MetadataRepository<RestaurantMetadata>,
    private val embeddingRepository: EmbeddingRepository<RestaurantEmbedding>
): DataStorageService<Restaurant> {
    private val logger = Logger.getLogger(TAG)
    private val updatedRestaurant: MutableSharedFlow<Restaurant> = MutableSharedFlow()

    override fun search(fieldName: String, topK: Int, vectorList: List<List<Float>>): List<List<Restaurant>> {
        val resultList = embeddingRepository.search(fieldName, topK, vectorList)
        val processedResultList = mutableListOf<List<Restaurant>>()

        resultList.map { embeddingList ->
            val idList = embeddingList.map { it.id }
            val metadataList = metadataRepository.get(idList)
            val embeddingMap = embeddingList.associateBy { it.id }
            val entityList = metadataList.map { metadata ->
                val embedding = embeddingMap[metadata.id]
                Restaurant.createRestaurant(metadata, embedding)
            }
            processedResultList.add(entityList)
        }

        return processedResultList
    }

    override fun insert(entityList: List<Restaurant>) {
        CoroutineScope(Dispatchers.IO).launch {
            val newEntityList = mutableListOf<Restaurant>()
            val embeddedEntityList = mutableListOf<Restaurant>()

            entityList.map {
                when(it.getStatus()) {
                    Status.NEW -> newEntityList.add(it)
                    Status.ANALYZED -> {
                        logger.severe("Attempted to insert ANALYZED restaurant with id=${it.getId()}")
                        throw IllegalArgumentException("ANALYZED entity cannot be inserted.")
                    }
                    Status.EMBEDDED -> embeddedEntityList.add(it)
                }
            }

            if(newEntityList.isNotEmpty()) {
                metadataRepository.insert(newEntityList.map { it.getMetadata() })
                newEntityList.map { updatedRestaurant.emit(it) }
            }
            if(embeddedEntityList.isNotEmpty()) {
                embeddingRepository.insert(embeddedEntityList.map { it.getEmbedding() })
            }
        }
    }

    override fun update(entityList: List<Restaurant>) {
        CoroutineScope(Dispatchers.IO).launch {
            val analyzedEntityList = mutableListOf<Restaurant>()
            val embeddedEntityList = mutableListOf<Restaurant>()

            entityList.map {
                when(it.getStatus()) {
                    Status.NEW -> {
                        logger.severe("Attempted to update NEW restaurant with id=${it.getId()}")
                        throw IllegalArgumentException("NEW entity cannot be updated.")
                    }
                    Status.ANALYZED -> analyzedEntityList.add(it)
                    Status.EMBEDDED -> embeddedEntityList.add(it)
                }
            }

            if(analyzedEntityList.isNotEmpty()) {
                metadataRepository.update(analyzedEntityList.map { it.getMetadata()})
                analyzedEntityList.map { updatedRestaurant.emit(it) }
            }
            if(embeddedEntityList.isNotEmpty()) {
                metadataRepository.update(embeddedEntityList.map { it.getMetadata() })
                embeddingRepository.upsert(embeddedEntityList.map { it.getEmbedding() })
            }
        }
    }

    override fun delete(idList: List<String>) {
        metadataRepository.delete(idList)
        embeddingRepository.delete(idList)
    }

    override fun get(idList: List<String>): List<Restaurant> {
        val metadataList = metadataRepository.get(idList)
        val embeddingList = embeddingRepository.get(idList)
        val embeddingMap = embeddingList.associateBy { it.id }

        val entityList = metadataList.map { metadata ->
            val embedding = embeddingMap[metadata.id]
            Restaurant.createRestaurant(metadata, embedding)
        }

        return entityList
    }

    override fun getAll(): List<Restaurant> {
        val metadataList = metadataRepository.getAll()
        val embeddingList = embeddingRepository.getAll()
        val embeddingMap = embeddingList.associateBy { it.id }

        return metadataList.map { metadata ->
            val embedding = embeddingMap[metadata.id]
            Restaurant.createRestaurant(metadata, embedding)
        }
    }

    override fun getAsFlow(): Flow<Restaurant> {
        return updatedRestaurant
    }

    companion object {
        const val TAG = "RestaurantService"
    }
}