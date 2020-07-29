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

}