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
import com.bobadilla.opentabledemo.adapters.MainAdapter
import com.bobadilla.opentabledemo.controller.ConnectionController
import com.bobadilla.opentabledemo.objects.CommonFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView

    private var lay: Int = 0
    private var mainAdapter: MainAdapter? = null

    private var JSONResponse: JSONObject? = null
    public var citiesList: ArrayList<String>? = null

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = getArguments()
        lay = bundle!!.getInt("lay")
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

    private fun initViews(view: View) {
        listView = view.findViewById(R.id.lv_cities)
        searchBar = view.findViewById(R.id.search_cities_bar)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = this!!.citiesList!![position]
            CommonFunctions.goToNextFragment(lay, selectedCity)
        }

        searchBar!!.setOnQueryTextListener(this)

        coroutineScope.launch {
            JSONResponse = ConnectionController.callOpenTableSync("https://opentable.herokuapp.com/api/cities").await()
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
            val citiesArray : JSONArray? = JSONResponse?.getJSONArray("cities")

            citiesList = ArrayList()

            for (i in 0 until citiesArray!!.length()) {
                citiesList?.add(citiesArray!!.getString(i))
            }

            mainAdapter = MainAdapter(citiesList!!,this)
            listView.adapter = mainAdapter
            mainAdapter?.notifyDataSetChanged()
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