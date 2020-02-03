package com.bobadilla.opentabledemo.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.Singleton
import com.bobadilla.opentabledemo.adapters.MainAdapter
import com.bobadilla.opentabledemo.connection.OkHttpRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList

class MainFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView

    private var lay: Int = 0
    private var mainAdapter: MainAdapter? = null
    private var mHandler = Handler(Looper.getMainLooper());

    private var JSONResponse: JSONObject? = null
    public var citiesList: ArrayList<String>? = null

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
            goToRestaurants(selectedCity)
        }

        searchBar!!.setOnQueryTextListener(this)

        callOpenTable()
    }

    fun goToRestaurants(selectedCity: String) {

        val restaurantsFragment = RestaurantsFragment()
        val bundle = Bundle()
        bundle.putInt("lay", lay)
        bundle.putString("city",selectedCity)
        restaurantsFragment.setArguments(bundle)
        getFragmentManager()?.beginTransaction()
            ?.replace(lay, restaurantsFragment)
            ?.addToBackStack(null)
            ?.commit()

    }

    private fun callOpenTable() {

        Singleton.showLoadDialog(getFragmentManager())

        var client = OkHttpClient()
        var request= OkHttpRequest(client)

        val url = "https://opentable.herokuapp.com/api/cities"

        request.GET(url, object: Callback {

            override fun onFailure(call: Call, e: IOException) {
                println("Request Failure.")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                mHandler.post{
                    try {
                        JSONResponse = JSONObject(responseData)
                        println("Request Successful!!")
                        println(JSONResponse)
                        this@MainFragment.fetchComplete()
                    } catch (e: JSONException) {
                        Singleton.dissmissLoad()
                        e.printStackTrace()
                        Singleton.showCustomDialog(Singleton.getFragmentManager(),
                            activity?.resources?.getString(R.string.connection_problem_title), activity?.resources?.getString(R.string.connection_problem_message), activity?.resources?.getString(R.string.connection_problem_action), 0);
                    }
                }
            }

        })
    }

    fun fetchComplete() {

        println("fetchComplete")

        val citiesArray : JSONArray? = JSONResponse?.getJSONArray("cities")

        citiesList = ArrayList()

        for (i in 0 until citiesArray!!.length()) {
            citiesList?.add(citiesArray!!.getString(i))
        }

        mainAdapter = MainAdapter(Singleton.getCurrentActivity(),citiesList!!,this)
        listView!!.adapter = mainAdapter
        mainAdapter!!.notifyDataSetChanged()

        Singleton.dissmissLoad()

    }

    override fun onQueryTextSubmit(query: String): Boolean {
        mainAdapter!!.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        mainAdapter!!.filter(newText)
        return false
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}