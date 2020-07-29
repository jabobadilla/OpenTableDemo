package com.bobadilla.opentabledemo.connection.api

import com.bobadilla.opentabledemo.data.models.CitiesArray
import com.bobadilla.opentabledemo.data.models.Restaurant
import retrofit2.http.GET

interface ConnectionService {

    @GET("cities")
    suspend fun loadCities() : CitiesArray

    @GET("restaurants?city=")
    suspend fun loadRestaurants(fragmentLoad: Boolean, selectedCity: String) : Restaurant

}