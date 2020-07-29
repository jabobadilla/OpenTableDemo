package com.bobadilla.opentabledemo.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.bobadilla.opentabledemo.data.dataLoad.DataNetLoad
import com.bobadilla.opentabledemo.data.database.RestaurantDao
import com.bobadilla.opentabledemo.data.database.RestaurantDatabase
import com.bobadilla.opentabledemo.data.models.CitiesArray
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
This class is pretty much self-explanatory.
It acts as the only access point to your data.
It passes your request for data through RestaurantDao to RestaurantDatabase and returns the data to
the requested view (Activity or Fragment).
 */

class RestaurantRepository(application: Application) {

    private val restaurantDao : RestaurantDao

    init {
        val restaurantDatabase = RestaurantDatabase.getInstance(application)
        restaurantDao = restaurantDatabase.restaurantDao()
    }

    fun getAllCities(): LiveData<List<City>> {
        return restaurantDao.getAllCitites()
    }

    fun findByCityName(name: String): LiveData<List<City>> {
        return restaurantDao.findBy(name)
    }

    fun findRestaurantsByCity(city: String) : LiveData<List<Restaurant>> {
        if (restaurantDao.countRestaurantsInCity(city) <= 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = DataNetLoad.loadRestaurants(true,city).await()
                for (restaurant in data) {
                    restaurantDao.insertRestaurant(restaurant)
                }
                restaurantDao.findByCity(city)
            }
        }
        return restaurantDao.findByCity(city)
    }

    fun findRestaurantsByName(name: String, city: String) : LiveData<List<Restaurant>> {
        return restaurantDao.findByRestaurantName(name, city)
    }

    fun findRestaurantByID(id: String) : LiveData<Restaurant> {
        return restaurantDao.findByID(id)
    }

    fun insertCity(cities : MutableList<City>) {
        restaurantDao.deleteAllCities()
        for (city in cities) {
            restaurantDao.insertCity(city)
        }
    }

}