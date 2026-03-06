package com.example.weather_forecast.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class FavoriteLocationStat(
    val cityName: String,
    val countryName: String,
    val countryCode: String,
    val temp: Int,
    val highTemp: Int,
    val lowTemp: Int,
    val weatherCondition: String,
    val humidity: Int,
    val windSpeed: Double,
    val iconUrl: String? = null
)