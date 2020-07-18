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
import com.bobadilla.opentabledemo.adapters.MainAdapter
import com.bobadilla.opentabledemo.objects.CommonFunctions

class MainFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView
    private var lay: Int = 0
    private var mainAdapter: MainAdapter? = null
    var citiesList: ArrayList<String>? = null
    private lateinit var model: RestaurantsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        lay = bundle!!.getInt("lay")
        model = ViewModelProvider(activity!!).get(RestaurantsViewModel::class.java)
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

    private fun initViews(view: View) {
        listView = view.findViewById(R.id.lv_cities)
        searchBar = view.findViewById(R.id.search_cities_bar)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = this!!.citiesList!![position]
            CommonFunctions.goToNextFragment(lay, selectedCity)
        }

        searchBar!!.setOnQueryTextListener(this)

        model.cities.observe(viewLifecycleOwner, Observer {
            if (mainAdapter == null) {
                citiesList = it
                mainAdapter = MainAdapter(citiesList!!,this)
            }
            listView.adapter = mainAdapter
            mainAdapter?.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        mainAdapter?.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        mainAdapter?.filter(newText)
        return false
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}