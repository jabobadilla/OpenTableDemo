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
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.objects.Restaurant
import com.bobadilla.opentabledemo.views.RestaurantsFragment
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class RestaurantsAdapter(private val dataSource: ArrayList<Restaurant>, private val fragment: RestaurantsFragment) : BaseAdapter() {
    private var inflater: LayoutInflater? = null
    private var dataSourceCopy : ArrayList<Restaurant> = ArrayList()

    init {
        dataSourceCopy.addAll(dataSource)
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        var img: ImageView? = null
        var txt: TextView? = null

        if (convertView == null) {
            inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater!!.inflate(R.layout.listview_cities_row, parent, false)
        }

        img = ViewHolder[convertView!!, R.id.main_icon]
        if (dataSource[position].image_url != null)
            Picasso.get().load(dataSource[position].image_url).into(img)
        else
            img.setImageDrawable(parent.resources.getDrawable(R.drawable.restaurantes,parent.context.theme))


        txt = ViewHolder[convertView, R.id.main_txt]
        txt.text = dataSource[position].name

        return convertView
    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        fragment.restaurantsList?.clear()
        if (charText.length == 0) {
            fragment.restaurantsList?.addAll(dataSourceCopy)
        } else {
            for (wp in dataSourceCopy) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    fragment.restaurantsList?.add(wp)
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
