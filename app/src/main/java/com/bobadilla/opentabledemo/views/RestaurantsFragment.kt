package com.bobadilla.opentabledemo.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.bobadilla.opentabledemo.connection.OkHttpRequest
import com.bobadilla.opentabledemo.objects.Restaurant
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class RestaurantsFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView

    private var lay: Int = 0
    private var selectedCity : String? = null
    private var restaurantsAdapter: RestaurantsAdapter? = null
    private var mHandler = Handler(Looper.getMainLooper());

    private var JSONResponse: JSONObject? = null
    public var restaurantsList: ArrayList<Restaurant>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = getArguments()
        lay = bundle!!.getInt("lay")
        selectedCity = bundle!!.getString("city", "")
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
            val selectedRestaurant = this!!.restaurantsList!![position].id
            goToRestaurantDetail(selectedRestaurant)
        }

        searchBar!!.setOnQueryTextListener(this)

        callOpenTable()
    }

    fun goToRestaurantDetail(selectedRestaurant: Int) {

        val restaurantsFragment = RestaurantDetailFragment()
        val bundle = Bundle()
        bundle.putInt("lay", lay)
        bundle.putInt("restaurant",selectedRestaurant)
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

        val url = "https://opentable.herokuapp.com/api/restaurants?city=" + selectedCity

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
                        this@RestaurantsFragment.fetchComplete()
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

        val restaurantsArray : JSONArray? = JSONResponse?.getJSONArray("restaurants")

        restaurantsList = ArrayList<Restaurant>()
        for (i in 0 until restaurantsArray!!.length()) {
            restaurantsList?.add(Gson().fromJson(restaurantsArray!!.getString(i),Restaurant::class.java))
        }

        restaurantsAdapter = RestaurantsAdapter(Singleton.getCurrentActivity(),restaurantsList!!,this)
        listView!!.adapter = restaurantsAdapter
        restaurantsAdapter!!.notifyDataSetChanged()

        Singleton.dissmissLoad()

    }

    override fun onQueryTextSubmit(query: String): Boolean {
        restaurantsAdapter!!.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        restaurantsAdapter!!.filter(newText)
        return false
    }

}