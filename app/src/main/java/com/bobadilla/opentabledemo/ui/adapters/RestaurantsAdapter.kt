package com.bobadilla.opentabledemo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bobadilla.opentabledemo.R
import com.bobadilla.opentabledemo.data.models.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.listview_cities_row.view.*

class RestaurantsAdapter(private val restaurants: List<Restaurant>, private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(restaurant: Restaurant, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_cities_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(restaurants[position], clickListener)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(restaurant: Restaurant, listener: OnItemClickListener) = with(itemView) {
            if (restaurant.image_url != null)
                Picasso.get().load(restaurant.image_url).into(mainIcon)
            else
                mainIcon.setImageResource(R.drawable.restaurantes)

            mainText.text = restaurant.name
            setOnClickListener {
                listener.onItemClick(restaurant,it)
            }
        }
    }

}
