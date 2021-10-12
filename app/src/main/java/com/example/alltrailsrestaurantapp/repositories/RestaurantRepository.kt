package com.example.alltrailsrestaurantapp.repositories

import com.example.alltrailsrestaurantapp.apis.RestaurantApi
import com.example.alltrailsrestaurantapp.database.dao.FavoriteRestaurantDao
import com.example.alltrailsrestaurantapp.database.entity.FavoriteRestaurantEntity
import com.example.alltrailsrestaurantapp.models.PlacesApiResponse
import com.example.alltrailsrestaurantapp.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RestaurantRepository {
    suspend fun fetchNearbyRestaurants(type: String?, location: String?, radius: Int):Flow<PlacesApiResponse>
    suspend fun insertFavoriteRestaurant(result:Result)
    suspend fun removeFavoriteRestaurant(result:Result)
    fun fetchFavoriteRestaurants():Flow<List<FavoriteRestaurantEntity>>
}

class RestaurantRepositoryImpl @Inject constructor(private val restaurantApi: RestaurantApi, private val restaurantDao: FavoriteRestaurantDao): RestaurantRepository{


    override suspend fun fetchNearbyRestaurants(type: String?, location: String?, radius: Int):Flow<PlacesApiResponse> {
        return flow {
         emit(restaurantApi.getNearbyPlaces(type, location, radius))
        }
    }

    override suspend fun insertFavoriteRestaurant(result:Result){
        if(result.geometry?.location?.lat !=null && result.geometry?.location?.lng != null && result.geometry?.location?.lat !=0.0 && result.geometry?.location?.lng != 0.0){
            restaurantDao.insertRestaurantLocation(FavoriteRestaurantEntity(result.geometry?.location?.lat ?:0.0,result.geometry?.location?.lng?:0.0, result.name, result.vicinity))
        }
    }

    override suspend fun removeFavoriteRestaurant(result: Result) {
        if(result.geometry?.location?.lat !=null && result.geometry?.location?.lng != null && result.geometry?.location?.lat !=0.0 && result.geometry?.location?.lng != 0.0){
            restaurantDao.deleteFavoriteRestaurant(FavoriteRestaurantEntity(result.geometry?.location?.lat ?:0.0,result.geometry?.location?.lng?:0.0, result.name, result.vicinity))
        }
    }

    override fun fetchFavoriteRestaurants():Flow<List<FavoriteRestaurantEntity>>{
        return restaurantDao.fetchFavoriteRestaurants()
    }

}