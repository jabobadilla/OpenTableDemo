package com.bobadilla.opentabledemo.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.Singleton
import com.bobadilla.opentabledemo.adapters.RestaurantsAdapter
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

class RestaurantsFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView

    private var lay: Int = 0
    private var selectedCity : String? = null
    private var restaurantsAdapter: RestaurantsAdapter? = null

    private var JSONResponse: JSONObject? = null
    public var restaurantsList: ArrayList<Restaurant>? = null

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = getArguments()
        lay = bundle!!.getInt("lay")
        selectedCity = bundle!!.getString("selectedItem", "")
        Singleton.setCurrentFragment(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        Singleton.setCurrentFragment(this)
    }

    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        initViews(rootView)
        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun initViews(view: View) {
        listView = view.findViewById(R.id.lv_cities)
        searchBar = view.findViewById(R.id.search_cities_bar)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedRestaurant = this!!.restaurantsList!![position].id.toString()
            CommonFunctions.goToNextFragment(lay, selectedRestaurant)
        }

        searchBar!!.setOnQueryTextListener(this)

        coroutineScope.launch {
            JSONResponse = ConnectionController.callOpenTableSync("https://opentable.herokuapp.com/api/restaurants?city=" + selectedCity).await()
            when (JSONResponse) {
                is JSONObject -> {
                    fetchComplete()
                }
                else -> CommonFunctions.displayJSONReadErrorMessage()
            }
        }
    }

    fun fetchComplete() {

        println("fetchComplete")

        try {
            val restaurantsArray : JSONArray? = JSONResponse?.getJSONArray("restaurants")

            restaurantsList = ArrayList<Restaurant>()
            for (i in 0 until restaurantsArray!!.length()) {
                restaurantsList?.add(Gson().fromJson(restaurantsArray!!.getString(i),Restaurant::class.java))
            }

            restaurantsAdapter = RestaurantsAdapter(restaurantsList!!,this)
            listView.adapter = restaurantsAdapter
            restaurantsAdapter?.notifyDataSetChanged()
        }
        catch (e: JSONException){
            e.printStackTrace()
            CommonFunctions.displayJSONReadErrorMessage()
        }
        finally {
            Singleton.dissmissLoad()
        }

    }

    override fun onQueryTextSubmit(query: String): Boolean {
        restaurantsAdapter?.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        restaurantsAdapter?.filter(newText)
        return false
    }

}