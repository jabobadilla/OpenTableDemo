package com.bobadilla.opentabledemo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.data.models.City
import kotlinx.android.synthetic.main.listview_cities_row.view.*


class CitiesAdapter(private val cities: List<City>, private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(city: City, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_cities_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(cities[position], clickListener)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(city: City, listener: OnItemClickListener) = with(itemView) {
            mainIcon.setImageResource(R.drawable.restaurantes)
            mainText.text = city.city
            setOnClickListener {
                listener.onItemClick(city,it)
            }
        }

    }

}
