package com.example.alltrailsrestaurantapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RestaurantApp: Application() {


    override fun onCreate() {
        super.onCreate()
    }
}