package com.entity

import com.common.Status

data class RestaurantMetadata(
    var id: String,
    var status: Status,
    var source: String = RestaurantProperty.SOURCE.defaultValue as String,
    var name: String = RestaurantProperty.NAME.defaultValue as String,
    var category: String = RestaurantProperty.CATEGORY.defaultValue as String,
    var phone: String = RestaurantProperty.PHONE.defaultValue as String,
    var address: String = RestaurantProperty.ADDRESS.defaultValue as String,
    var x: Double = RestaurantProperty.X.defaultValue as Double,
    var y: Double = RestaurantProperty.Y.defaultValue as Double,
    var reviews: List<String> = RestaurantProperty.REVIEWS.defaultValue as List<String>,
    var businessDays: String = RestaurantProperty.BUSINESS_DAYS.defaultValue as String,
    var url: String = RestaurantProperty.URL.defaultValue as String,
    var hasWifi: Boolean? = null,
    var hasParking: Boolean? = null,
    var menus: List<Any> = RestaurantProperty.MENUS.defaultValue as List<Any>,
    var minPrice: Int = RestaurantProperty.MIN_PRICE.defaultValue as Int,
    var maxPrice: Int = RestaurantProperty.MAX_PRICE.defaultValue as Int,
    var mood: String = RestaurantProperty.MOOD.defaultValue as String,
    var taste: String = RestaurantProperty.TASTE.defaultValue as String,
)
