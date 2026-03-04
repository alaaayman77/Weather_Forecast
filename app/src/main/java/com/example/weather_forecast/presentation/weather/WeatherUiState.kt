package com.example.weather_forecast.presentation.weather

import com.example.weather_forecast.data.models.WeatherResponse


    sealed class WeatherUiState {
        object Idle : WeatherUiState()
        object Loading : WeatherUiState()
        data class Success(val weather: WeatherResponse) : WeatherUiState()
        data class Error(val message: String) : WeatherUiState()
    }
