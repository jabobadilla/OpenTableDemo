package com.bobadilla.opentabledemo.connection.api

import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIRetriever {

    private val service : ConnectionService

    companion object {
        const val BASE_URL = "https://opentable.herokuapp.com/api/"
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ConnectionService::class.java)
    }

    suspend fun getAPICities() : MutableList<City> {
        val citiesArray = service.loadCities()
        var citiesList = mutableListOf<City>()
        for (i in 0 until citiesArray!!.cities.size) {
            citiesList.add(City(i, citiesArray.cities[i]))
        }
        return citiesList
    }

    suspend fun getAPIRestaurants(fragmentLoad: Boolean, selectedCity: String) : Restaurant {
        return service.loadRestaurants(fragmentLoad, selectedCity)
    }
}