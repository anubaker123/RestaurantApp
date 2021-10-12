package com.example.alltrailsrestaurantapp.di

import com.example.alltrailsrestaurantapp.repositories.RestaurantRepository
import com.example.alltrailsrestaurantapp.repositories.RestaurantRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsRestaurantRepository(restaurantRepositoryImpl: RestaurantRepositoryImpl): RestaurantRepository

}