package com.bobadilla.opentabledemo.objects

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.Singleton
import com.bobadilla.opentabledemo.Singleton.getFragmentManager
import com.bobadilla.opentabledemo.views.MainFragment
import com.bobadilla.opentabledemo.views.RestaurantDetailFragment
import com.bobadilla.opentabledemo.views.RestaurantsFragment

object CommonFunctions {

    fun displayJSONReadErrorMessage() {
        Singleton.showCustomDialog(
            Singleton.getFragmentManager(),
            Singleton.getCurrentActivity()?.resources?.getString(R.string.json_read_problem_title),
            Singleton.getCurrentActivity()?.resources?.getString(
                R.string.json_read_problem_message
            ),
            Singleton.getCurrentActivity()?.resources?.getString(
                R.string.json_read_problem_action
            ),
            0
        )
    }

    fun displayConnectionProblemMessage() {
        Singleton.showCustomDialog(
            Singleton.getFragmentManager(),
            Singleton.getCurrentActivity()?.resources?.getString(R.string.connection_problem_title),
            Singleton.getCurrentActivity()?.resources?.getString(
                R.string.connection_problem_message
            ),
            Singleton.getCurrentActivity()?.resources?.getString(
                R.string.connection_problem_action
            ),
            0
        )
    }

    fun goToNextFragment(lay: Int, selectedItem: String) {

        val currentFragment = Singleton.getCurrentFragment()
        val fragment: Fragment =
            when (currentFragment) {
                is MainFragment -> RestaurantsFragment()
                is RestaurantsFragment -> RestaurantDetailFragment()
                else -> RestaurantDetailFragment()
        }
        val bundle = Bundle()
        bundle.putInt("lay", lay)
        bundle.putString("selectedItem",selectedItem)
        fragment.setArguments(bundle)
        getFragmentManager()?.beginTransaction()
            ?.replace(lay, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

}