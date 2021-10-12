package com.example.alltrailsrestaurantapp.di

import android.content.Context
import androidx.room.Room
import com.example.alltrailsrestaurantapp.database.AppDatabase
import com.example.alltrailsrestaurantapp.database.dao.FavoriteRestaurantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppDatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext appContext: Context): AppDatabase{

        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "AppDatabase"
        ).build()
    }

    @Provides
    fun providesFavoriteRestaurantDao(appDatabase: AppDatabase): FavoriteRestaurantDao {
        return appDatabase.favoriteRestaurantDao()
    }

}