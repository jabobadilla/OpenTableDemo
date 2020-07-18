package com.bobadilla.opentabledemo.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bobadilla.opentabledemo.Singleton
import com.bobadilla.opentabledemo.controller.ConnectionController
import com.bobadilla.opentabledemo.objects.CommonFunctions
import com.bobadilla.opentabledemo.objects.Restaurant
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RestaurantsViewModel(val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY = "Saved_Cities_List"
    }

    private var JSONResponse: JSONObject? = null
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    var previousSelectedCity: String? = null
    var previousSelectedRestaurant: String? = null
    private val _cities: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>().also {
            loadCities()
        }
    }
    var restaurantsInCity: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData(ArrayList())
    var restaurantDetail: MutableLiveData<Restaurant>? = MutableLiveData(Restaurant(0,"","","","","","","","",0.toFloat(),0.toFloat(),0,"","",""))
    val cities: LiveData<ArrayList<String>>
    get() = _cities
    var selectedCity: String? = null
    var selectedRestaurant: String? = null

    private fun loadCities() {
        coroutineScope.launch {
            JSONResponse = ConnectionController.callOpenTableSync("https://opentable.herokuapp.com/api/cities").await()
            when (JSONResponse) {
                is JSONObject -> {
                    try {
                        val citiesArray : JSONArray? = JSONResponse?.getJSONArray("cities")
                        var citiesList: ArrayList<String> = ArrayList()
                        for (i in 0 until citiesArray!!.length()) {
                            citiesList.add(citiesArray.getString(i))
                        }
                        _cities.postValue(citiesList) // If the code is executed in a worker thread, other than the main thread, you can use the postValue(T) method to update the LiveData object.
                        //cities.value?.addAll(citiesList) //We must use the setValue(T) method to update the LiveData object from the main thread
                    }
                    catch (e: JSONException){
                        e.printStackTrace()
                        CommonFunctions.displayJSONReadErrorMessage()
                    }
                    finally {
                        Singleton.dissmissLoad()
                    }
                }
                else -> CommonFunctions.displayJSONReadErrorMessage()
            }
        }
    }

    fun loadRestaurants(selectedCity: String) {

        if (previousSelectedCity == null || previousSelectedCity != selectedCity) {
            coroutineScope.launch {
                JSONResponse = ConnectionController.callOpenTableSync("https://opentable.herokuapp.com/api/restaurants?city=$selectedCity" ).await()
                when (JSONResponse) {
                    is JSONObject -> {
                        try {
                            val restaurantsArray : JSONArray? = JSONResponse?.getJSONArray("restaurants")
                            var restaurantsList = ArrayList<Restaurant>()
                            for (i in 0 until restaurantsArray!!.length()) {
                                restaurantsList.add(Gson().fromJson(restaurantsArray.getString(i),Restaurant::class.java))
                            }
                            restaurantsInCity?.postValue(restaurantsList)
                            previousSelectedCity = selectedCity
                        }
                        catch (e: JSONException){
                            e.printStackTrace()
                            CommonFunctions.displayJSONReadErrorMessage()
                        }
                        finally {
                            Singleton.dissmissLoad()
                        }
                    }
                    else -> CommonFunctions.displayJSONReadErrorMessage()
                }
            }
        }
    }

    fun loadRestaurantDetail(selectedRestaurant: String) {
        if (previousSelectedRestaurant == null || previousSelectedRestaurant != selectedRestaurant) {
            coroutineScope.launch {
                JSONResponse = ConnectionController.callOpenTableSync("https://opentable.herokuapp.com/api/restaurants/$selectedRestaurant").await()
                when (JSONResponse) {
                    is JSONObject -> {
                        try {
                            val restaurant = Gson().fromJson(JSONResponse.toString(),Restaurant::class.java)
                            restaurantDetail?.postValue(restaurant)
                            previousSelectedRestaurant = selectedRestaurant
                        }
                        catch (e: JSONException){
                            e.printStackTrace()
                            CommonFunctions.displayJSONReadErrorMessage()
                        }
                        finally {
                            Singleton.dissmissLoad()
                        }
                    }
                    else -> CommonFunctions.displayJSONReadErrorMessage()
                }
            }
        }
    }

    /*fun getCities(): LiveData<ArrayList<String>> {
        //return savedStateHandle.getLiveData<ArrayList<String>>(KEY)
        return cities
    }

    fun addCitiesToArray(array: ArrayList<String>) {
        getCities().value?.addAll(array)
        savedStateHandle.set(KEY,getCities().value)
    }*/

}