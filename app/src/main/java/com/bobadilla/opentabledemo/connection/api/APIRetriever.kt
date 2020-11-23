package com.bobadilla.opentabledemo.connection.api

import com.bobadilla.opentabledemo.common.CommonFunctions
import com.bobadilla.opentabledemo.common.Singleton
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant
import com.google.gson.Gson
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

    suspend fun getAPICities(fragmentLoad: Boolean) : MutableList<City> {
        if (fragmentLoad) CommonFunctions.showLoadDialog()
        val citiesArray = service.loadCities()
        var citiesList = mutableListOf<City>()
        for ((index, city) in citiesArray.cities.withIndex()) {
            citiesList.add(City(index, city))
        }
        CommonFunctions.dismissLoadDialog()
        return citiesList
    }

    suspend fun getAPIRestaurantsByCity(fragmentLoad: Boolean, selectedCity: String) : MutableList<Restaurant> {
        if (fragmentLoad) CommonFunctions.showLoadDialog()
        val restaurantsResult = service.loadRestaurantsByCity(selectedCity)
        var restaurantsList = mutableListOf<Restaurant>()
        restaurantsList = restaurantsResult.restaurants.toMutableList()
        CommonFunctions.dismissLoadDialog()
        return restaurantsList
    }

}