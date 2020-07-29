package com.bobadilla.opentabledemo.data.dataLoad

import com.bobadilla.opentabledemo.common.CommonFunctions
import com.bobadilla.opentabledemo.common.Singleton
import com.bobadilla.opentabledemo.connection.controller.ConnectionController
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object DataNetLoad {

    private var JSONResponse: JSONObject? = null
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    suspend fun loadCities(fragmentLoad: Boolean): Deferred<MutableList<City>> =
        coroutineScope.async(start = CoroutineStart.LAZY) {
            var citiesList = mutableListOf<City>()
            JSONResponse =
                ConnectionController.callOpenTableSync(fragmentLoad, "https://opentable.herokuapp.com/api/cities")
                    .await()
            when (JSONResponse) {
                is JSONObject -> {
                    try {
                        val citiesArray: JSONArray? = JSONResponse?.getJSONArray("cities")
                        for (i in 0 until citiesArray!!.length()) {
                            citiesList.add(City(i, citiesArray.getString(i)))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        CommonFunctions.displayJSONReadErrorMessage()
                    } finally {
                        Singleton.dissmissLoad()
                    }
                }
                else -> CommonFunctions.displayJSONReadErrorMessage()
            }
            return@async citiesList
        }

    suspend fun loadRestaurants(fragmentLoad: Boolean, selectedCity: String) : Deferred<MutableList<Restaurant>> =
        coroutineScope.async(start = CoroutineStart.LAZY) {
            var restaurantsList = mutableListOf<Restaurant>()
            JSONResponse = ConnectionController.callOpenTableSync(fragmentLoad,"https://opentable.herokuapp.com/api/restaurants?city=$selectedCity").await()
            when (JSONResponse) {
                is JSONObject -> {
                    try {
                        val restaurantsArray: JSONArray? = JSONResponse?.getJSONArray("restaurants")
                        for (i in 0 until restaurantsArray!!.length()) {
                            restaurantsList.add(Gson().fromJson(restaurantsArray.getString(i), Restaurant::class.java))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        CommonFunctions.displayJSONReadErrorMessage()
                    } finally {
                        Singleton.dissmissLoad()
                    }
                }
                else -> CommonFunctions.displayJSONReadErrorMessage()
            }
            return@async restaurantsList
        }

}