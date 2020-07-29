package com.bobadilla.opentabledemo.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.bobadilla.opentabledemo.data.RestaurantRepository
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant

class RestaurantsViewModel(application: Application, city: String) : AndroidViewModel(application) {

    private val restaurantRepository = RestaurantRepository(application)
    private val restaurantsList = MediatorLiveData<List<Restaurant>>()

    init {
        restaurantsList.addSource(restaurantRepository.findRestaurantsByCity(city)) { restaurants ->
            restaurantsList.postValue(restaurants)
        }
    }

    fun getRestaurantsByCity() : LiveData<List<Restaurant>> {
        return restaurantsList
    }

    fun searchRestaurantsByName(name: String, city: String) {
        restaurantsList.addSource(restaurantRepository.findRestaurantsByName(name, city)) { restaurants ->
            restaurantsList.postValue(restaurants)
        }
    }
}

class RestaurantsViewModelFactory(private val application: Application, private val city: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RestaurantsViewModel(application, city) as T
    }
}