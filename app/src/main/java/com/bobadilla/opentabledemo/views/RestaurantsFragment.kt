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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.Singleton
import com.bobadilla.opentabledemo.ViewModels.RestaurantsViewModel
import com.bobadilla.opentabledemo.adapters.RestaurantsAdapter
import com.bobadilla.opentabledemo.objects.CommonFunctions
import com.bobadilla.opentabledemo.objects.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONObject

class RestaurantsFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView
    private var lay: Int = 0
    private var selectedCity : String? = null
    private var restaurantsAdapter: RestaurantsAdapter? = null
    var restaurantsList: ArrayList<Restaurant>? = null
    private lateinit var model: RestaurantsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = Singleton.getRestaurantsViewModel()
        val bundle = arguments
        lay = bundle!!.getInt("lay")
        if (savedInstanceState != null) {
            selectedCity = model.selectedCity
        } else {
            selectedCity = bundle!!.getString("selectedCity", "")
            model.selectedCity = selectedCity
        }
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

        model.restaurantsInCity?.observe(viewLifecycleOwner, Observer {
            if (model.previousSelectedCity == selectedCity) {
                if (restaurantsList == null) {
                    restaurantsList = it
                    restaurantsAdapter = RestaurantsAdapter(restaurantsList!!,this)
                }
            }
            listView.adapter = restaurantsAdapter
            restaurantsAdapter?.notifyDataSetChanged()
        })

        model.loadRestaurants(selectedCity!!)

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