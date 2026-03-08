package com.example.weather_forecast.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherInfoItem(
    val icon: Int,
    val value: String,
    val label: String
)

data class WeeklyWeatherForecast(
    val day: String,
    val icon: ImageVector,
    val lowTemp: Int,
    val highTemp: Int,
    val condition: String = "",
    val iconUrl: String? = null
)