package com.example.alltrailsrestaurantapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.alltrailsrestaurantapp.database.dao.FavoriteRestaurantDao
import com.example.alltrailsrestaurantapp.database.entity.FavoriteRestaurantEntity

@Database(version = 1, exportSchema = false, entities = [FavoriteRestaurantEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteRestaurantDao() :FavoriteRestaurantDao
}