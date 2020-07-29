package com.bobadilla.opentabledemo.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.data.models.City
import com.bobadilla.opentabledemo.ui.adapters.CitiesAdapter
import com.bobadilla.opentabledemo.ui.viewModels.CitiesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), CitiesAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var searchBar : SearchView
    private lateinit var citiesViewModel: CitiesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        citiesViewModel = ViewModelProvider(requireActivity()).get(CitiesViewModel::class.java)
    }

    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        searchBar = rootView.findViewById(R.id.searchBar)
        searchBar.setOnQueryTextListener(this)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citiesViewModel.getCitiesList().observe(viewLifecycleOwner, Observer { cities ->
            rvCities.adapter = CitiesAdapter(cities, this)
        })

        requireActivity().title = getString(R.string.app_name)
        requireActivity().toolbar.title = getString(R.string.app_name)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        citiesViewModel.searchCities(query)
        hideKeyboardFromSearchBar()
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        citiesViewModel.searchCities(newText)
        return true
    }

    override fun onItemClick(city: City, itemView: View) {
        hideKeyboardFromSearchBar()
        val bundle = Bundle().apply {
            putString(getString(R.string.selected_city), city.city)
        }
        view?.findNavController()?.navigate(R.id.action_mainFragment_to_restaurantsFragment,bundle)
    }

    fun hideKeyboardFromSearchBar() {
        searchBar.clearFocus()
    }

}