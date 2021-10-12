package com.example.alltrailsrestaurantapp.apis

import com.example.alltrailsrestaurantapp.models.PlacesApiResponse
import retrofit2.http.GET
import com.example.alltrailsrestaurantapp.util.Constants
import retrofit2.http.Query


interface RestaurantApi {
    @GET("api/place/nearbysearch/json?sensor=true&key="+Constants.GOOGLE_MAPS_KEY)
    suspend fun getNearbyPlaces(
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: Int
    ): PlacesApiResponse
}