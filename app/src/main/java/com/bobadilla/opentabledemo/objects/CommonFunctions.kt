package com.bobadilla.opentabledemo.objects

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
        val selected: String =
            when (Singleton.getCurrentFragment()) {
                is MainFragment -> {
                    if (selectedItem.equals("selectedItem")) selectedItem else "selectedCity"
                }
                is RestaurantsFragment -> "selectedRestaurant"
                else -> "selectedItem"
            }
        val fragment: Fragment =
            when (Singleton.getCurrentFragment()) {
                is MainFragment -> {
                    val test = getFragmentManager().backStackEntryCount
                    if (getFragmentManager().backStackEntryCount <= 0 && selected.equals("selectedItem"))
                        MainFragment()
                    else
                        RestaurantsFragment()
                }
                is RestaurantsFragment -> RestaurantDetailFragment()
                else -> MainFragment()
        }

        val bundle = Bundle()
        bundle.putInt("lay", lay)
        bundle.putString(selected,selectedItem)
        fragment.setArguments(bundle)
        val fm = getFragmentManager()?.beginTransaction()
        when (fragment) {
            is MainFragment -> {
                fm?.replace(lay, fragment, fragment.javaClass.toString())?.commit()
            }
            else -> {
                    fm?.replace(lay, fragment, fragment.javaClass.toString())
                    ?.addToBackStack( fragment.javaClass.toString() )
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    ?.commit()
            }
        }

    }

}