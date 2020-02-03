package com.bobadilla.opentabledemo.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.Singleton
import com.bobadilla.opentabledemo.connection.OkHttpRequest
import com.bobadilla.opentabledemo.objects.Restaurant
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class RestaurantDetailFragment : Fragment(), View.OnClickListener {
    private var lay = 0
    private var selectedRestaurant : Int = 0
    private var mHandler = Handler(Looper.getMainLooper());

    private var JSONResponse: JSONObject? = null
    private var restaurant: Restaurant? = null
    private val CALL_REQUEST = 100
    private var parsedString: String? = null

    private var img_row: ImageView? = null
    private var name_row: TextView? = null
    private  var address_row:TextView? = null
    private  var city_row:TextView? = null
    private  var state_row:TextView? = null
    private  var area_row:TextView? = null
    private  var postalCode_row:TextView? = null
    private  var country_row:TextView? = null
    private  var phone_row:TextView? = null
    private  var price_row:TextView? = null
    private  var reserve_row:TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = getArguments()
        lay = bundle!!.getInt("lay")
        selectedRestaurant = bundle!!.getInt("restaurant", 0)
        Singleton.setCurrentFragment(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.row_restaurant_detail, container, false)
        initViews(rootView)
        return rootView
    }

    private fun initViews(view: View) {
        img_row = view.findViewById(R.id.img_row)
        name_row = view.findViewById(R.id.name_row)
        address_row = view.findViewById(R.id.address_row)
        city_row = view.findViewById(R.id.city_row)
        state_row = view.findViewById(R.id.state_row)
        area_row = view.findViewById(R.id.area_row)
        postalCode_row = view.findViewById(R.id.postalCode_row)
        country_row = view.findViewById(R.id.country_row)
        phone_row = view.findViewById(R.id.phone_row)
        price_row = view.findViewById(R.id.price_row)
        reserve_row = view.findViewById(R.id.reserve_row)

        reserve_row?.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(restaurant?.reserve_url.toString())
            startActivity(openURL)
        }

        phone_row?.setOnClickListener {
            parsedString = restaurant?.phone.toString()
            callPhoneNumber(parsedString!!)
        }

        callOpenTable()
    }

    override fun onClick(v: View) {}

    private fun callOpenTable() {

        var client = OkHttpClient()
        var request= OkHttpRequest(client)

        val url = "https://opentable.herokuapp.com/api/restaurants/" + selectedRestaurant

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
                        this@RestaurantDetailFragment.fetchComplete()
                    } catch (e: JSONException) {
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

        restaurant = Gson().fromJson(JSONResponse.toString(),Restaurant::class.java)

        if (restaurant?.image_url != null)
            Picasso.get().load(restaurant?.image_url).into(img_row)
        else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img_row?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.rest_header,activity?.theme))
            }

        name_row?.text = restaurant?.name
        address_row?.text = "Address: " + restaurant?.address
        city_row?.text = "City: " + restaurant?.city
        state_row?.text = "State: " + restaurant?.state
        area_row?.text = "Area: " + restaurant?.area
        postalCode_row?.text = "Zip Code: " + restaurant?.postalCode
        country_row?.text = "Country: " + restaurant?.country
        if (restaurant?.phone != null || restaurant?.phone != "")
            phone_row?.text = "Phone: " + restaurant?.phone + "   (PRESS HERE TO DIAL)"
        else
            phone_row?.visibility = View.GONE
        when (restaurant?.price) {
            1 -> price_row?.text = "$"
            2 -> price_row?.text = "$$"
            3 -> price_row?.text = "$$$"
            else -> price_row?.text = "$$$$"
        }
        if (restaurant?.reserve_url != null)
            reserve_row?.text = "RESERVE HERE (PRESS)"
        else
            reserve_row?.visibility = View.GONE
    }

    private fun callPhoneNumber(tel: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST)
                    return
                }
            }
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + tel.trim { it <= ' ' })
            activity!!.startActivity(callIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber(parsedString!!)
            } else {
                Toast.makeText(activity, activity!!.resources.getString(R.string.call_permission_denied_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

}