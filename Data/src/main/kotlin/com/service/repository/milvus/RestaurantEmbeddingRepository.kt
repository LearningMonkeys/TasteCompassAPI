package com.service.repository.milvus

import com.entity.RestaurantEmbedding
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.vector.request.*
import io.milvus.v2.service.vector.request.data.FloatVec
import org.springframework.stereotype.Repository as SpringRepository
import java.util.logging.Logger

@SpringRepository
class RestaurantEmbeddingRepository(
    private val milvusClient: MilvusClientV2
): EmbeddingRepository<RestaurantEmbedding> {
    private val logger: Logger = Logger.getLogger(TAG)

    override fun search(fieldName: String, topK: Int, vectorList: List<List<Float>>): List<List<RestaurantEmbedding>> {
        val vectorData = vectorList.map { FloatVec(it) }
        val searchReq = SearchReq.builder()
            .collectionName(COLLECTION_NAME)
            .annsField(fieldName)
            .data(vectorData)
            .topK(topK)
            .outputFields(OUTPUT_FIELDS)
            .build()

        val searchResp = milvusClient.search(searchReq)
        val ret = mutableListOf<MutableList<RestaurantEmbedding>>()
        searchResp.searchResults.forEach { results ->
            val topKResults = mutableListOf<RestaurantEmbedding>()
            results.forEach { result ->
                val restaurantEmbedding = RestaurantEmbedding.fromMap(result.entity)
                topKResults.add(restaurantEmbedding)
            }
            ret.add(topKResults)
        }

        return ret
    }

    override fun insert(entityList: List<RestaurantEmbedding>) {
        val dataList = entityList.map { it.toJsonObject() }
        val insertReq = InsertReq.builder()
            .collectionName(COLLECTION_NAME)
            .data(dataList)
            .build()

        milvusClient.insert(insertReq)
    }

    override fun upsert(entityList: List<RestaurantEmbedding>) {
        val dataList = entityList.map { it.toJsonObject() }
        val upsertReq = UpsertReq.builder()
            .collectionName(COLLECTION_NAME)
            .data(dataList)
            .build()

        milvusClient.upsert(upsertReq)
    }

    override fun delete(idList: List<String>) {
        val deleteReq = DeleteReq.builder()
            .collectionName(COLLECTION_NAME)
            .ids(idList)
            .build()

        milvusClient.delete(deleteReq)
    }

    override fun get(idList: List<String>): List<RestaurantEmbedding> {
        val getReq = GetReq.builder()
            .collectionName(COLLECTION_NAME)
            .ids(idList)
            .build()

        val getResp = milvusClient.get(getReq)
        val entityList = mutableListOf<RestaurantEmbedding>()
        getResp.getResults.forEach { getResult ->
            entityList.add(RestaurantEmbedding.fromMap(getResult.entity))
        }

        return entityList
    }

    override fun getAll(): List<RestaurantEmbedding> {
        val queryReq = QueryReq.builder()
            .collectionName(COLLECTION_NAME)
            .filter("id > -1")
            .build()

        val queryResp = milvusClient.query(queryReq)
        val entityList = mutableListOf<RestaurantEmbedding>()
        queryResp.queryResults.forEach { queryResult ->
            entityList.add(RestaurantEmbedding.fromMap(queryResult.entity))
        }

        return entityList
    }

    companion object {
        private const val COLLECTION_NAME = "Restaurant"
        private const val TAG = "RestaurantRepository"
        private val OUTPUT_FIELDS = listOf(
            "id",
            "category",
            "address",
            "x",
            "y",
            "business_days",
            "has_wifi",
            "has_parking",
            "min_price",
            "max_price",
            "mood_vector",
            "taste_vector"
        )
    }
}