package com.bobadilla.opentabledemo.adapters

import android.content.Context
import android.os.Build
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bobadilla.opentabledemo.views.MainFragment
import java.util.*
import kotlin.collections.ArrayList
import com.bobadilla.opentabledemo.R


class MainAdapter(private val context: Context, private val dataSource: ArrayList<String>, private val fragment: MainFragment) : BaseAdapter() {
    private var inflater: LayoutInflater? = null
    private var dataSourceCopy : ArrayList<String> = ArrayList()

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dataSourceCopy.addAll(dataSource)
    }

    override fun getCount(): Int {
        return dataSource.size
        //return titles.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
        //return titles[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        var img: ImageView? = null
        var txt: TextView? = null

        if (convertView == null)
            convertView = inflater!!.inflate(R.layout.listview_cities_row, parent, false)

        img = ViewHolder[convertView!!, R.id.main_icon]
        //img.setImageResource(icons.getResourceId(position, -1))
        img.setImageDrawable(context.resources.getDrawable(R.drawable.restaurantes,context.theme))

        txt = ViewHolder[convertView, R.id.main_txt]
        //txt.text = titles[position]
        txt.text = dataSource[position]

        return convertView
    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        fragment.citiesList?.clear()
        if (charText.length == 0) {
            fragment.citiesList?.addAll(dataSourceCopy)
        } else {
            for (wp in dataSourceCopy) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    fragment.citiesList?.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

    object ViewHolder {
        operator fun <T : View> get(view: View, id: Int): T {
            var test = view.tag
            var viewHolder: SparseArray<View>? = null
            if (test != null) {
                viewHolder= view.tag as SparseArray<View>
            }

            if (viewHolder == null) {
                viewHolder = SparseArray()
                view.tag = viewHolder
            }
            var childView: View? = viewHolder.get(id)
            if (childView == null) {
                childView = view.findViewById(id)
                viewHolder.put(id, childView)
            }
            return childView as T
        }
    }

}
