package com.entity

import com.common.Constants
import com.common.Status

enum class RestaurantProperty(val key: String, val defaultValue: Any) {
    ID("id", "N/A"),
    STATUS("status", Status.NEW),
    SOURCE("source", "N/A"),
    NAME("name", "N/A"),
    CATEGORY("category", "N/A"),
    PHONE("phone", "N/A"),
    ADDRESS("address", "N/A"),
    X("x", 0.0),
    Y("y", 0.0),
    REVIEWS("reviews", emptyList<String>()),
    BUSINESS_DAYS("business_days", "N/A"),
    URL("url", "N/A"),
    HAS_WIFI("has_wifi", false),
    HAS_PARKING("has_parking", false),
    MENUS("menus", emptyList<Any>()),
    MIN_PRICE("min_price", 0),
    MAX_PRICE("max_price", 0),
    MOOD("mood", "N/A"),
    MOOD_VECTOR("mood_vector", List(Constants.EMBEDDING_SIZE) { 0.0f }),
    TASTE("taste", "N/A"),
    TASTE_VECTOR("taste_vector", List(Constants.EMBEDDING_SIZE) { 0.0f });
}
