package com.bobadilla.opentabledemo.common

import com.bobadilla.opentabledemo.R

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

    fun displayNoInternetConnectionMessage() {
        val test1 = Singleton.getCurrentActivity()
        val test = Singleton.getCurrentActivity()?.resources?.getString(R.string.connection_problem_title)
        Singleton.showCustomDialog(
            Singleton.getFragmentManager(),
            Singleton.getCurrentActivity()?.resources?.getString(R.string.internet_connection_problem_title),
            Singleton.getCurrentActivity()?.resources?.getString(
                R.string.internet_connection_problem_message
            ),
            Singleton.getCurrentActivity()?.resources?.getString(
                R.string.internet_connection_problem_action
            ),
            0
        )
    }

    fun showLoadDialog() {
        Singleton.showLoadDialog(Singleton.getFragmentManager())
    }

    fun dismissLoadDialog() {
        Singleton.dissmissLoad()
    }

}