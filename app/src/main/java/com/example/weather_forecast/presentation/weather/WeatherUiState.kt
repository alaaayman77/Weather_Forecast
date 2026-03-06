package com.example.weather_forecast.presentation.weather

import com.example.weather_forecast.data.models.OneCallResponse



sealed class WeatherUiState {
    object Idle    : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val data: OneCallResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
