package com.bobadilla.opentabledemo.objects

import com.google.gson.annotations.SerializedName

data class RestaurantResults (
    @SerializedName("total_entries") val totalEntries: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("restaurants") val restaurants: ArrayList<Restaurant>
)

data class Restaurant(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("area") val area: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("price") val price: Int,
    @SerializedName("reserve_url") val reserve_url: String,
    @SerializedName("mobile_reserve") val mobile_reserve: String,
    @SerializedName("image_url") val image_url: String
)
