package com.example.alltrailsrestaurantapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Geometry {
    /**
     *
     * @return
     * The location
     */
    /**
     *
     * @param location
     * The location
     */
    @SerializedName("location")
    @Expose
    var location: Location? = null
}