package com.bobadilla.opentabledemo.ui.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.ui.viewModels.RestaurantDetailViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_restaurant_detail.*

class RestaurantDetailFragment : Fragment() {

    private val CALL_REQUEST = 100
    private var parsedString: String? = null
    private lateinit var restaurantDetailViewModel: RestaurantDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restaurantDetailViewModel = ViewModelProvider(requireActivity()).get(RestaurantDetailViewModel::class.java)
    }

    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.row_restaurant_detail, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantDetailViewModel.getRestaurantDetail(arguments?.getString(getString(R.string.selected_restaurant))!!).observe(viewLifecycleOwner, Observer { restaurant ->
            if (restaurant?.image_url != null)
                Picasso.get().load(restaurant?.image_url).into(headerImageRow)
            else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    headerImageRow?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.rest_header,activity?.theme))
                }

            nameRow?.text = restaurant?.name
            addressRow?.text = "Address: " + restaurant?.address
            cityRow?.text = "City: " + restaurant?.city
            stateRow?.text = "State: " + restaurant?.state
            areaRow?.text = "Area: " + restaurant?.area
            postalCodeRow?.text = "Zip Code: " + restaurant?.postalCode
            countryRow?.text = "Country: " + restaurant?.country
            if (restaurant?.phone != null || restaurant?.phone != "")
                phoneRow?.text = "Phone: " + restaurant?.phone + "   (PRESS HERE TO DIAL)"
            else
                phoneRow?.visibility = View.GONE
            when (restaurant?.price) {
                1 -> priceRow?.text = "$"
                2 -> priceRow?.text = "$$"
                3 -> priceRow?.text = "$$$"
                else -> priceRow?.text = "$$$$"
            }
            if (restaurant?.reserve_url != null)
                reserveRow?.text = "RESERVE HERE (PRESS)"
            else
                reserveRow?.visibility = View.GONE

            reserveRow?.setOnClickListener {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(restaurant?.reserve_url.toString())
                startActivity(openURL)
            }

            phoneRow?.setOnClickListener {
                parsedString = restaurant?.phone.toString()
                callPhoneNumber(parsedString!!)
            }
        })
    }

    private fun callPhoneNumber(tel: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST)
                    return
                }
            }
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + tel.trim { it <= ' ' })
            requireActivity().startActivity(callIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber(parsedString!!)
            } else {
                Toast.makeText(activity, requireActivity().resources.getString(R.string.call_permission_denied_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

}