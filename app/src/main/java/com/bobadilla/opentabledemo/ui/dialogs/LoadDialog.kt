package com.bobadilla.opentabledemo.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bobadilla.opentabledemo.R

class LoadDialog : DialogFragment() {

    var loadText: TextView? = null

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        val style = DialogFragment.STYLE_NORMAL
        val theme = android.R.style.Theme_Holo
        setStyle(style, theme)
    }

    override fun onCreateDialog(saveInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.dialog_load, null)
        builder.setView(v)

        loadText = v.findViewById<View>(R.id.loadText) as TextView

        return builder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            Log.e("solved super error", "solved super error OK")
        } else
            super.onSaveInstanceState(outState)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

    companion object {

        fun newInstance(): LoadDialog {

            return LoadDialog()
        }
    }

}
