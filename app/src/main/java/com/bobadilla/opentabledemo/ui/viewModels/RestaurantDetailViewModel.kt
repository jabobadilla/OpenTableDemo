package com.bobadilla.opentabledemo.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.bobadilla.opentabledemo.data.RestaurantRepository
import com.bobadilla.opentabledemo.data.models.Restaurant

class RestaurantDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val restaurantRepository = RestaurantRepository(application)
    private val restaurantId = MutableLiveData<String>()

    fun getRestaurantDetail(id: String) : LiveData<Restaurant> {
        restaurantId.value = id
        val test = restaurantId.hasActiveObservers()
        return Transformations.switchMap(restaurantId) {id ->
            restaurantRepository.findRestaurantByID(id)
        }
    }
}