package com.bobadilla.opentabledemo.connection.api

import com.bobadilla.opentabledemo.data.models.CitiesJSONResults
import com.bobadilla.opentabledemo.data.models.RestaurantJSONResults
import retrofit2.http.GET
import retrofit2.http.Query

interface ConnectionService {

    @GET("cities")
    suspend fun loadCities() : CitiesJSONResults

    @GET("restaurants")
    suspend fun loadRestaurantsByCity(@Query("city") selectedCity: String) : RestaurantJSONResults

}