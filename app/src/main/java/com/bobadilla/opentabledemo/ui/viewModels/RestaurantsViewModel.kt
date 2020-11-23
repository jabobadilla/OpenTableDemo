package com.bobadilla.opentabledemo.ui.viewModels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.bobadilla.opentabledemo.data.RestaurantRepository
import com.bobadilla.opentabledemo.data.models.Restaurant

@RequiresApi(Build.VERSION_CODES.M)
class RestaurantsViewModel(val applications: Application, val context: Context, val city: String) : AndroidViewModel(applications) {

    class Factory(val application: Application, val context: Context, val city: String) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RestaurantsViewModel(application, context, city) as T
        }
    }

    private val restaurantRepository = RestaurantRepository(applications)
    private var restaurantsList = MediatorLiveData<List<Restaurant>>()
    private val restaurantsCity = MutableLiveData<String>()
    private var searchText : String = ""

    init {
        getRestaurants(city)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getRestaurants(city: String) {
        restaurantsList.addSource(fetchRestaurantsByCity(city)) { restaurants ->
            restaurantsList.postValue(restaurants)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun fetchRestaurantsByCity(city: String) : LiveData<List<Restaurant>> {
        restaurantsCity.value = city
        return Transformations.switchMap(restaurantsCity) { cityName ->
            restaurantRepository.findRestaurantsByCity(applications, context, cityName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getRestaurantResults(city: String) : LiveData<List<Restaurant>> {
        if (searchText == "") { fetchRestaurantsByCity(city) }
        return restaurantsList
    }

    fun searchRestaurantsByName(name: String, city: String) {
        searchText = name
        restaurantsList.addSource(restaurantRepository.findRestaurantsByName(name, city)) { restaurants ->
            if (city == restaurants[0].city) { restaurantsList.postValue(restaurants) }
        }
    }

}