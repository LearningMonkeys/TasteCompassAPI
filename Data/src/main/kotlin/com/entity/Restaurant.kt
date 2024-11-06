package com.entity

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Restaurant(
    var id: Long,
    var name: String? = null,
    var minPrice: Float? = null,
    var maxPrice: Float? = null,
    var reviews:List<String> = mutableListOf(),
    var mood: String? = null,
    var moodVector: List<Float>? = null) {

    fun toReadableString(): String {
        return "[$id]$name, price $minPrice ~ $maxPrice , review count : ${reviews.size}, mood: $mood, moodVectorExists : ${moodVector != null}"
    }


    fun toJsonObject(): JsonObject {
        val jsonObject = JsonObject().apply {
            add("mood_vector", JsonArray().apply {
                (moodVector ?: List(1536) { 0.0f }).forEach { add(it) }
            })
            addProperty("id", id)
            addProperty("name", name ?: "N/A")
            addProperty("min_price", minPrice ?: -1f)
            addProperty("max_price", maxPrice ?: -1f)
            add("reviews", JsonArray())
            addProperty("mood", mood ?: "N/A")
        }

        return jsonObject
    }

    companion object {
        fun fromJsonObject(jsonObject: JsonObject): Restaurant {
            return Restaurant(
                id = jsonObject.get("id").asLong,
                maxPrice = jsonObject.get("max_price")?.asDouble?.takeIf { it >= 0 }?.toFloat(),
                minPrice = jsonObject.get("min_price")?.asDouble?.takeIf { it >= 0 }?.toFloat(),
                moodVector = jsonObject.getAsJsonArray("mood_vector")?.let { moodVectorToList(it) },
                name = jsonObject.get("name")?.asString?.takeIf { it != "N/A" },
                reviews = kotlin.runCatching {
                    val reviewsData = jsonObject.getAsJsonObject("reviews")
                        ?.getAsJsonObject("Data")
                        ?.getAsJsonObject("StringData")
                        ?.getAsJsonArray("data")
                    reviewsData?.let { reviewsToList(it) } ?: listOf()
                }.getOrDefault(listOf())
            )
        }

        fun fromMap(map: Map<String, Any>): Restaurant {
            return Restaurant(
                id = map["id"] as? Long ?: -1L,
                name = map["name"] as? String,
                minPrice = (map["min_price"] as? Number)?.toFloat(),
                maxPrice = (map["max_price"] as? Number)?.toFloat(),
                reviews = (map["reviews"] as? List<*>)?.filterIsInstance<String>() ?: listOf(),
                mood = map["mood"] as? String,
                moodVector = (map["mood_vector"] as? List<*>)?.filterIsInstance<Number>()?.map { it.toFloat() }
            )
        }

        private fun moodVectorToList(jsonArray: JsonArray): List<Float> {
            val floatList = mutableListOf<Float>()
            for (i in 0 until jsonArray.size()) {
                val value = jsonArray[i].asFloat
                floatList.add(value)
            }
            return floatList
        }

        private fun reviewsToList(jsonArray: JsonArray): List<String> {
            val reviewList = mutableListOf<String>()
            for (i in 0 until jsonArray.size()) {
                val value = jsonArray[i].asString
                reviewList.add(value)
            }
            return reviewList
        }

    }
}