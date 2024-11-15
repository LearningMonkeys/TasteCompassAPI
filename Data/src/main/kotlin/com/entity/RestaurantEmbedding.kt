package com.entity

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.logging.Logger

data class RestaurantEmbedding(
    val id: String,
    var category: String = RestaurantProperty.CATEGORY.defaultValue as String,
    val address: String = RestaurantProperty.ADDRESS.defaultValue as String,
    var x: Double = RestaurantProperty.X.defaultValue as Double,
    var y: Double = RestaurantProperty.Y.defaultValue as Double,
    var businessDays: String = RestaurantProperty.BUSINESS_DAYS as String,
    var hasWifi: Boolean? = null,
    var hasParking: Boolean? = null,
    var minPrice: Int = RestaurantProperty.MIN_PRICE.defaultValue as Int,
    var maxPrice: Int = RestaurantProperty.MAX_PRICE.defaultValue as Int,
    var moodVector: List<Float> = RestaurantProperty.MOOD_VECTOR.defaultValue as List<Float>,
    var tasteVector: List<Float> = RestaurantProperty.TASTE_VECTOR.defaultValue as List<Float>,
) {
    private val logger = Logger.getLogger(TAG)

    fun toJsonObject(): JsonObject {
        val jsonObject = JsonObject().apply {
            addProperty("id", id)
            addProperty("category", category)
            addProperty("address", address)
            addProperty("x", x)
            addProperty("y", y)
            addProperty("business_days", businessDays)
            hasWifi?.let { addProperty("has_wifi", it) }
            hasParking?.let { addProperty("has_parking", it) }
            addProperty("min_price", minPrice)
            addProperty("max_price", maxPrice)
            add("mood_vector", JsonArray().apply {
                moodVector.forEach { add(it) }
            })
            add("taste_vector", JsonArray().apply {
                tasteVector.forEach { add(it) }
            })
        }

        return jsonObject
    }

    companion object {
        private const val TAG = "RestaurantEmbedding"

        fun fromMap(map: Map<String, Any>): RestaurantEmbedding {
            return RestaurantEmbedding(
                id = map["id"] as String,
                category = map["category"] as? String ?: RestaurantProperty.CATEGORY.defaultValue as String,
                x = map["x"] as? Double ?: RestaurantProperty.X.defaultValue as Double,
                y = map["x"] as? Double ?: RestaurantProperty.Y.defaultValue as Double,
                businessDays = map["x"] as? String ?: RestaurantProperty.BUSINESS_DAYS.defaultValue as String,
                hasWifi = map["has_wifi"] as? Boolean,
                hasParking = map["has_parking"] as? Boolean,
                minPrice = map["min_price"] as? Int ?: RestaurantProperty.MIN_PRICE.defaultValue as Int,
                maxPrice = map["max_price"] as? Int ?: RestaurantProperty.MAX_PRICE.defaultValue as Int,
                moodVector = (map["mood_vector"] as? List<*>)?.filterIsInstance<Float>()
                    ?: RestaurantProperty.MOOD_VECTOR.defaultValue as List<Float>,
                tasteVector = (map["taste_vector"] as? List<*>)?.filterIsInstance<Float>()
                    ?: RestaurantProperty.TASTE_VECTOR.defaultValue as List<Float>
            )
        }
    }
}