package com.bobadilla.opentabledemo.data

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bobadilla.opentabledemo.common.CommonFunctions
import com.bobadilla.opentabledemo.connection.ConnectionStatus
import com.bobadilla.opentabledemo.connection.api.*
import com.bobadilla.opentabledemo.data.database.DatabaseExists
import com.bobadilla.opentabledemo.data.database.RestaurantDao
import com.bobadilla.opentabledemo.data.database.RestaurantDatabase
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.data.models.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NullPointerException

/*
This class is pretty much self-explanatory.
It acts as the only access point to your data.
It passes your request for data through RestaurantDao to RestaurantDatabase and returns the data to
the requested view (Activity or Fragment).
 */

class RestaurantRepository(application: Application) : APICallsHandler(), APIErrorEmitter {

    private val restaurantDao : RestaurantDao

    init {
        val restaurantDatabase = RestaurantDatabase.getInstance(application)
        restaurantDao = restaurantDatabase.restaurantDao()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkNetworkAndDatabase(application: Application, context: Context) : Boolean {
        if (ConnectionStatus.isNetworkConnected(application.applicationContext)
            && DatabaseExists.databaseFileExists(context)) {
            return true
        }
        return false
    }

    /***********************************************************************************************
     * All Cities Functions
     **********************************************************************************************/

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun fetchCitiesFromServer(application: Application, context: Context) : List<City> {
        var data = mutableListOf<City>()
        if (checkNetworkAndDatabase(application, context)) {
            //data = APIRetriever().getAPICities(false)
            val test = safeAPICall(this@RestaurantRepository) { APIRetriever().getAPICities(false) }
            if (test != null) { data = test.toMutableList() }
            }
        return data
    }

    fun citiesListsAreEqual(databaseList: List<City>, serverList: List<City>) : Boolean {
        if (databaseList.size != serverList.size) { return false }

        for((index, databaseCity) in databaseList.withIndex()) {
            if (databaseCity.city != serverList[index].city) { return false }
        }
        return true
    }

    fun combineCitiesFetchResults(databaseData: LiveData<List<City>>, serverData: MutableLiveData<List<City>>): List<City> {
        var finalList = mutableListOf<City>()
        var databaseList = mutableListOf<City>()
        var serverList = mutableListOf<City>()

        try {
            if (databaseData != null) databaseList.addAll(databaseData.value!!)
            if (serverData != null) serverList.addAll(serverData.value!!)

            if (citiesListsAreEqual(databaseList, serverList)) {
                finalList.addAll(databaseData.value!!)
            }
            else {
                finalList.addAll(serverData.value!!)
                insertCity(finalList)
            }
        }
        catch (e: NullPointerException) {
            print(e.localizedMessage)
        }
        return finalList
    }

    fun insertCity(cities: List<City>) {
        restaurantDao.deleteAllCities()
        for (city in cities) {
            restaurantDao.insertCity(city)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getAllCities(applications: Application, context: Context): LiveData<List<City>> {

        val databaseData = restaurantDao.getAllCitites()
        val serverData = MutableLiveData<List<City>>()
        val result = MediatorLiveData<List<City>>()

        if (checkNetworkAndDatabase(applications, context)) {
            CoroutineScope(Dispatchers.IO).launch {
                serverData.postValue(fetchCitiesFromServer(applications, context))
            }

            result.addSource(databaseData) { value ->
                result.value = combineCitiesFetchResults(databaseData, serverData)
            }
            result.addSource(serverData) { value ->
                result.value = combineCitiesFetchResults(databaseData, serverData)
            }
        }
        else {
            result.addSource(restaurantDao.getAllCitites()) {
                result.value = it
                if (it.size <= 0) { CommonFunctions.displayNoInternetConnectionMessage() }
            }

        }

        return result
    }

    fun findByCityName(name: String): LiveData<List<City>> {
        return restaurantDao.findBy(name)
    }

    /***********************************************************************************************
     * Restaurants in Cities Functions
     **********************************************************************************************/

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun fetchRestaurantsInCityFromServer(application: Application, context: Context, showLoadDialog: Boolean, city: String) : List<Restaurant> {
        var data = mutableListOf<Restaurant>()
        if (checkNetworkAndDatabase(application, context)) {
            data = APIRetriever().getAPIRestaurantsByCity(showLoadDialog, city)
        }
        return data
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun restaurantsInCitiesListsAreEqual(databaseList: MutableList<Restaurant>, serverList: MutableList<Restaurant>, city: String) : Boolean {
        databaseList.sortBy { it.id.toString() }
        serverList.sortBy { it.id.toString() }

        serverList.removeIf { it.city != city }

        if (databaseList.size != serverList.size) { return false }

        databaseList.forEachIndexed { index, restaurant -> if (serverList[index] != restaurant) { return false} }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun combineRestaurantsInCityFetchResults(databaseData: LiveData<List<Restaurant>>, serverData: MutableLiveData<List<Restaurant>>, city: String): List<Restaurant> {
        var finalList = mutableListOf<Restaurant>()
        var databaseList = mutableListOf<Restaurant>()
        var serverList = mutableListOf<Restaurant>()

        try {
            if (databaseData != null) databaseList = databaseData.value?.toMutableList()!!
            if (serverData != null) serverList = serverData.value?.toMutableList()!!

            if (restaurantsInCitiesListsAreEqual(databaseList, serverList, city)) {
                finalList.addAll(databaseData.value!!)
            }
            else {
                finalList.addAll(serverData.value!!)
                insertRestaurantsInCity(finalList)
            }
        }
        catch (e: NullPointerException) {
            print(e.localizedMessage)
        }
        return finalList
    }

    fun insertRestaurantsInCity(restaurants: List<Restaurant>) {
        restaurantDao.deleteAllRestaurantsInCity(restaurants[0].city)
        for (restaurant in restaurants) {
            restaurantDao.insertRestaurant(restaurant)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun findRestaurantsByCity(applications: Application, context: Context, city: String) : LiveData<List<Restaurant>> {

        val databaseData = restaurantDao.findByCity(city)
        val serverData = MutableLiveData<List<Restaurant>>()
        val result = MediatorLiveData<List<Restaurant>>()

        if (checkNetworkAndDatabase(applications, context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val showLoadDialog = if (restaurantDao.countRestaurantsInCity(city) > 0) {false} else {true}
                serverData.postValue(fetchRestaurantsInCityFromServer(applications, context, showLoadDialog, city))
            }

            result.addSource(databaseData) { value ->
                result.value = combineRestaurantsInCityFetchResults(databaseData, serverData, city)
            }
            result.addSource(serverData) {
                result.value = combineRestaurantsInCityFetchResults(databaseData, serverData, city)
            }
        }
        else {
            result.addSource(restaurantDao.findByCity(city)) {
                result.value = it
                if (it.size <= 0) { CommonFunctions.displayNoInternetConnectionMessage() }
            }
        }

        return result
    }

    fun findRestaurantsByName(name: String, city: String) : LiveData<List<Restaurant>> {
        return restaurantDao.findByRestaurantName(name, city)
    }

    fun findRestaurantByID(id: String) : LiveData<Restaurant> {
        return restaurantDao.findByID(id)
    }

    override fun onError(msg: String) {
        CommonFunctions.displayConnectionProblemMessage()
    }

    override fun onError(errorType: ErrorType) {
        TODO("Not yet implemented")
    }

}