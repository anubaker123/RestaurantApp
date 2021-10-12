package com.example.alltrailsrestaurantapp.database.entity

import androidx.room.Entity
import com.example.alltrailsrestaurantapp.models.Result

@Entity(tableName = "favoriteRestaurant",  primaryKeys = ["latitude", "longitude"])
data class FavoriteRestaurantEntity(val latitude:Double, val longitude: Double, val name:String?, val location:String?){
    fun isSameLocation(result: Result):Boolean{
        if(result.geometry?.location?.lat == latitude && result.geometry?.location?.lng == longitude )
            return true
        return false
    }
}
