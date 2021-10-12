package com.example.alltrailsrestaurantapp.database.dao

import androidx.room.*
import com.example.alltrailsrestaurantapp.database.entity.FavoriteRestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurantLocation(favoriteRestaurantEntity: FavoriteRestaurantEntity): Long

    @Delete
    suspend fun deleteFavoriteRestaurant(vararg restaurant: FavoriteRestaurantEntity)

    @Query("SELECT * FROM favoriteRestaurant")
    fun fetchFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>>
}