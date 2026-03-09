package com.example.weather_forecast.presentation.weather


import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.OneCallResponse





sealed class UiState<out T> {
    object Idle    : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class WeatherState(
    val oneCall:        OneCallResponse,
    val topBarLocation: String = "--",
    val centerLocation: String = "--"
)

data class FavouriteState(
    val favourites: List<FavouriteEntity> = emptyList()
)