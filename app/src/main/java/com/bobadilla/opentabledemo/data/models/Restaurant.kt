package com.bobadilla.opentabledemo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/*
Entity: A model class that represents a table in a Room database.
 */

data class RestaurantJSONResults (
    @SerializedName("total_entries") val totalEntries : Int,
    @SerializedName("per_page") val perPage : Int,
    @SerializedName("current_page") val currentPage : Int,
    @SerializedName("restaurants") val restaurants : ArrayList<Restaurant>
)

@Entity
data class Restaurant (
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true) var id : Int = 0,
    @SerializedName("name") var name : String = "",
    @SerializedName("address") var address : String = "",
    @SerializedName("city") var city : String = "",
    @SerializedName("state") var state : String = "",
    @SerializedName("area") var area : String = "",
    @SerializedName("postal_code") var postalCode : String = "",
    @SerializedName("country") var country : String = "",
    @SerializedName("phone") var phone : String = "",
    @SerializedName("latitude") var latitude : Float = 0F,
    @SerializedName("longitude") var longitude : Float = 0F,
    @SerializedName("price") var price : Int = 0,
    @SerializedName("reserve_url") var reserve_url : String = "",
    @SerializedName("mobile_reserve") var mobile_reserve : String = "",
    @SerializedName("image_url") var image_url : String = ""
)