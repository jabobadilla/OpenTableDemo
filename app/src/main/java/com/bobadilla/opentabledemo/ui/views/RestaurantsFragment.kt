package com.bobadilla.opentabledemo.ui.views

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.data.models.Restaurant
import com.bobadilla.opentabledemo.ui.adapters.RestaurantsAdapter
import com.bobadilla.opentabledemo.ui.viewModels.RestaurantsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class RestaurantsFragment : Fragment(), RestaurantsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var searchBar : SearchView
    private var selectedCity : String? = null
    private lateinit var restaurantsViewModel: RestaurantsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCity = arguments?.getString(getString(R.string.selected_city))

        restaurantsViewModel =
            ViewModelProvider(requireActivity(),RestaurantsViewModel.Factory(requireActivity().application,requireActivity(),selectedCity!!))
            .get(RestaurantsViewModel::class.java)
    }

    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        searchBar = rootView.findViewById(R.id.searchBar)
        searchBar.setOnQueryTextListener(this)

        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantsViewModel.getRestaurantResults(selectedCity!!).observe(viewLifecycleOwner, Observer { restaurants ->
            rvCities.adapter = RestaurantsAdapter(restaurants, this )
        })

        requireActivity().title =  "Restaurants in $selectedCity"
        requireActivity().toolbar.title =  "Restaurants in $selectedCity"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onQueryTextSubmit(query: String): Boolean {
        restaurantsViewModel.searchRestaurantsByName(query, selectedCity!!)
        hideKeyboardFromSearchBar()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onQueryTextChange(newText: String): Boolean {
        restaurantsViewModel.searchRestaurantsByName(newText, selectedCity!!)
        return true
    }

    override fun onItemClick(restaurant: Restaurant, itemView: View) {
        hideKeyboardFromSearchBar()
        val bundle = Bundle().apply {
            putString(getString(R.string.selected_restaurant), restaurant.id.toString())
        }
        view?.findNavController()?.navigate(R.id.action_restaurantsFragment_to_restaurantDetailFragment2, bundle)
    }

    fun hideKeyboardFromSearchBar() {
        searchBar.clearFocus()
    }

}