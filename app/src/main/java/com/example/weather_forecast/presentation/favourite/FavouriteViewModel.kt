package com.example.weather_forecast.presentation.favourite

import com.example.weather_forecast.presentation.weather.FavouriteState

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.FavoriteLocationStat
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.navigation.NavigationRoutes
import com.example.weather_forecast.presentation.weather.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<FavouriteState>>(UiState.Idle)
    val uiState: StateFlow<UiState<FavouriteState>>
        get() = _uiState

    init {
        loadFavourites()
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            repository.getAllFavourites()
                .catch { _uiState.value = UiState.Error(it.message ?: "Error") }
                .collect { list ->
                    _uiState.value = UiState.Success(FavouriteState(favourites = list))
                }
        }

    }


    fun addFavourite(item: FavouriteEntity) {
        viewModelScope.launch {
            repository.addFavourite(item)
        }
    }

    fun removeFavourite(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.removeFavourite(lat, lon)
        }
    }

}

class FavouriteViewModelFactory(private val repository: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repository) as T
    }
}