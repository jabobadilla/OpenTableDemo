package com.bobadilla.opentabledemo.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bobadilla.opentabledemo.data.RestaurantRepository
import com.bobadilla.opentabledemo.data.models.City

/*
ViewModels offer a number of benefits:

1) ViewModel‘s are lifecycle-aware, which means they know when the attached Activity or Fragment is destroyed
and can immediately release data observers and other resources.
2) They survive configuration changes, so if your data is observed or fetched through a ViewModel, it’s still
available after your Activity or Fragment is re-created. This means you can re-use the data without fetching it again.
3) ViewModel takes the responsibility of holding and managing data. It acts as a bridge between your Repository and the
View. Freeing up your Activity or Fragment from managing data allows you to write more concise and unit-testable code.
 */

class CitiesViewModel(application: Application) : AndroidViewModel(application) {

    private val restaurantRepository = RestaurantRepository(application)
    private val citiesList = MediatorLiveData<List<City>>()

    init {
        getAllCities()
    }

    fun getAllCities() {
        citiesList.addSource(restaurantRepository.getAllCities()) { cities ->
            citiesList.postValue(cities)
        }
    }

    fun getCitiesList() : LiveData<List<City>> {
        return citiesList
    }

    fun searchCities(name: String) {
        citiesList.addSource(restaurantRepository.findByCityName(name)) { cities ->
            citiesList.postValue(cities)
        }
    }

}