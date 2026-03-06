package com.example.weather_forecast.presentation.favourite

import com.example.weather_forecast.presentation.weather.FavouriteState

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.models.FavoriteLocationStat
import com.example.weather_forecast.presentation.weather.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<FavouriteState>>(UiState.Idle)
    val uiState: StateFlow<UiState<FavouriteState>>
        get() = _uiState

    init {
        loadFavourites()
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val mockData = listOf(
                    FavoriteLocationStat(
                        cityName         = "Cairo",
                        countryName      = "Egypt",
                        countryCode      = "EG",
                        temp             = 24,
                        highTemp         = 28,
                        lowTemp          = 16,
                        weatherCondition = "Clear Sky",
                        humidity         = 54,
                        windSpeed        = 12.0,
                        iconUrl          = null
                    ),
                    FavoriteLocationStat(
                        cityName         = "London",
                        countryName      = "United Kingdom",
                        countryCode      = "UK",
                        temp             = 9,
                        highTemp         = 12,
                        lowTemp          = 6,
                        weatherCondition = "Light Rain",
                        humidity         = 82,
                        windSpeed        = 7.0,
                        iconUrl          = null
                    )
                )
                _uiState.value = UiState.Success(FavouriteState(favourites = mockData))
            } catch (ex: Exception) {
                _uiState.value = UiState.Error(ex.message ?: "Unknown error")
            }
        }
    }

}

class FavouriteViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel() as T
    }
}