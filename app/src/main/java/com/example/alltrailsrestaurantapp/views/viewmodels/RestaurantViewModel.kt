package com.example.alltrailsrestaurantapp.views.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alltrailsrestaurantapp.R
import com.example.alltrailsrestaurantapp.models.Result
import com.example.alltrailsrestaurantapp.repositories.RestaurantRepository
import com.example.alltrailsrestaurantapp.util.ErrorMessageWrapper
import com.example.alltrailsrestaurantapp.util.SingleLiveEvent
import com.example.alltrailsrestaurantapp.util.handleErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(private val restaurantRepository: RestaurantRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<List<Result>>(emptyList())
    val uiState: StateFlow<List<Result>> = _uiState

    private val _clearFields = SingleLiveEvent<Unit>()
    val clearFields: LiveData<Unit>
        get() = _clearFields

    private val mutableErrorMessage = SingleLiveEvent<ErrorMessageWrapper>()
    val errorMessage: LiveData<ErrorMessageWrapper>
        get() = mutableErrorMessage

    //Receivers
    val currentLocationChannel = Channel<Location>(Channel.CONFLATED)
    val searchChannel = Channel<String>(Channel.CONFLATED)
    val sortChannel = Channel<Boolean>(Channel.CONFLATED)
    val addFavoriteRestaurantChannel = Channel<Result>(Channel.CONFLATED)
    val removeFavoriteRestaurantChannel = Channel<Result>(Channel.CONFLATED)

    private var restaurants = emptyList<Result>()

    init {
        currentLocationChannel
            .receiveAsFlow()
            .flatMapLatest {
                restaurantRepository.fetchNearbyRestaurants(
                    "restaurant",
                    it.latitude.toString() + "," + it.longitude.toString(),
                    50000
                )
            }
            .combine(restaurantRepository.fetchFavoriteRestaurants()){ allRestaurants, favRestaurants ->
                allRestaurants.results.map { result ->
                    var isFavorite = false
                    favRestaurants.forEach { favRestaurant ->
                        if(favRestaurant.isSameLocation(result))
                            isFavorite = true
                    }
                    result.isFavorite = isFavorite
                }
                return@combine  allRestaurants
            }
            .onEach {
                this.restaurants = it.results
                _uiState.value = it.results
                _clearFields.postValue(Unit)
            }
            .handleErrors(R.string.unexpected_error, mutableErrorMessage)
            .launchIn(viewModelScope)

        searchChannel.receiveAsFlow()
            .onEach { it ->
                val searchTxt: String = it
                _uiState.value = this.restaurants.filter {
                    it.name?.startsWith(searchTxt, true) == true
                }
            }
            .handleErrors(R.string.unexpected_error, mutableErrorMessage)
            .launchIn(viewModelScope)

        sortChannel.receiveAsFlow()
            .onEach { it ->
                if(it == true)
                    _uiState.value = this.restaurants.sortedBy {
                        it.name?:""
                    }
                if(it == false)
                    _uiState.value = this.restaurants
            }
            .handleErrors(R.string.unexpected_error, mutableErrorMessage)
            .launchIn(viewModelScope)

        addFavoriteRestaurantChannel.receiveAsFlow()
            .onEach {
                restaurantRepository.insertFavoriteRestaurant(result = it)
            }
            .launchIn(viewModelScope)

        removeFavoriteRestaurantChannel.receiveAsFlow()
            .onEach {
                restaurantRepository.removeFavoriteRestaurant(result = it)
            }
            .handleErrors(R.string.unexpected_error, mutableErrorMessage)
            .launchIn(viewModelScope)
    }

}