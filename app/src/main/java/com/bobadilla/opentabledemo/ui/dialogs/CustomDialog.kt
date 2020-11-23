package com.bobadilla.opentabledemo.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.common.Singleton

class CustomDialog : DialogFragment(), View.OnClickListener {

    private var title: String? = null
    private var body: String? = null
    private var action: String? = null
    private var actionId: Int = 0

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        val style = DialogFragment.STYLE_NORMAL
        val theme = android.R.style.Theme_Holo

        title = arguments!!.getString("title")
        body = arguments!!.getString("body")
        action = arguments!!.getString("action")

        actionId = arguments!!.getInt("actionId")

        setStyle(style, theme)
    }

    override fun onCreateDialog(saveInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.dialog_custom, null)
        builder.setView(v)

        //val titleTv = v.findViewById<View>(R.id.title) as? TextView
        val titleTv: TextView? = v.findViewById(R.id.title)
        titleTv?.text = title

        val bodyTv: TextView? = v.findViewById(R.id.body)
        //bodyTv.setText(body);
        bodyTv?.text = Html.fromHtml(body)

        val actionBtn: Button? = v.findViewById(R.id.action)
        actionBtn?.text = action
        actionBtn?.setOnClickListener(this)

        val cancel: Button? = v.findViewById(R.id.cancel)
        cancel?.setOnClickListener(this)
        if (actionId == 100)
            cancel?.visibility = View.VISIBLE
        else
            cancel?.visibility = View.INVISIBLE

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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.action -> {
                if (actionId == 0) {
                    Singleton.dissmissCustom()
                }
                Singleton.dissmissCustom()
            }
            R.id.cancel -> Singleton.dissmissCustom()
        }
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

        fun newInstance(title: String, body: String, action: String, actionId: Int): CustomDialog {
            val custoDialog = CustomDialog()

            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("body", body)
            bundle.putString("action", action)
            bundle.putInt("actionId", actionId)
            custoDialog.arguments = bundle

            return custoDialog
        }
    }

}
