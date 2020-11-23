package com.bobadilla.opentabledemo.ui.viewModels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.bobadilla.opentabledemo.MainActivity
import com.bobadilla.opentabledemo.connection.ConnectionStatus
import com.bobadilla.opentabledemo.connection.api.APIRetriever
import com.bobadilla.opentabledemo.data.RestaurantRepository
import com.bobadilla.opentabledemo.data.database.DatabaseExists
import com.bobadilla.opentabledemo.data.models.City
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
ViewModels offer a number of benefits:

1) ViewModel‘s are lifecycle-aware, which means they know when the attached Activity or Fragment is destroyed
and can immediately release data observers and other resources.
2) They survive configuration changes, so if your data is observed or fetched through a ViewModel, it’s still
available after your Activity or Fragment is re-created. This means you can re-use the data without fetching it again.
3) ViewModel takes the responsibility of holding and managing data. It acts as a bridge between your Repository and the
View. Freeing up your Activity or Fragment from managing data allows you to write more concise and unit-testable code.
 */

@RequiresApi(Build.VERSION_CODES.M)
class CitiesViewModel(val applications: Application, val context: Context) : AndroidViewModel(applications) {

    class Factory(val application: Application, val context: Context) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CitiesViewModel(application, context) as T
        }
    }

    private val restaurantRepository = RestaurantRepository(applications)
    private val citiesList = MediatorLiveData<List<City>>()

    init {
        getAllCities()
    }

    fun getAllCities() {
        citiesList.addSource(restaurantRepository.getAllCities(applications, context)) { cities ->
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