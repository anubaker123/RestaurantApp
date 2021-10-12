package com.example.alltrailsrestaurantapp.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.alltrailsrestaurantapp.R
import com.example.alltrailsrestaurantapp.models.Result
import com.squareup.picasso.Picasso

class RestaurantsListAdapter(var context:Context?, var dataSet: List<Result>, val favFilledDrawable: Drawable?, val favBorderDrawable: Drawable?, var updateFavoriteRestaurant:(result: Result, isFavorite:Boolean) ->Unit) :
    RecyclerView.Adapter<RestaurantsListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantNameTxtView: TextView
        val restaurantVicinityTxtView: TextView
        val restaurantIcon: ImageView
        val favToggleButton: ToggleButton

        init {
            // Define click listener for the ViewHolder's View.
            restaurantNameTxtView = view.findViewById(R.id.restaurant_name)
            restaurantVicinityTxtView = view.findViewById(R.id.restaurant_address)
            restaurantIcon = view.findViewById(R.id.img_restaurant)
            favToggleButton = view.findViewById(R.id.favBtn)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.restaurantNameTxtView.text = dataSet.get(position).name
        viewHolder.restaurantVicinityTxtView.text = dataSet.get(position).vicinity
        if(context != null)
            Picasso.with(context).load(dataSet.get(position).icon).fit().into(viewHolder.restaurantIcon);
        if(dataSet.get(position).isFavorite)
            viewHolder.favToggleButton.background = favFilledDrawable
        if(!dataSet.get(position).isFavorite)
            viewHolder.favToggleButton.background = favBorderDrawable
        viewHolder.favToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (dataSet.get(position).isFavorite) {
                viewHolder.favToggleButton.background = favBorderDrawable
                updateFavoriteRestaurant(dataSet.get(position), false)
            } else {
                viewHolder.favToggleButton.background = favFilledDrawable
                updateFavoriteRestaurant(dataSet.get(position), true)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
