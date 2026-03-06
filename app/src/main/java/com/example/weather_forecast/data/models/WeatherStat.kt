package com.example.weather_forecast.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherStat(
    val icon: ImageVector,
    val value: String,
    val label: String
)
data class HourlyWeatherStat(
    val icon: ImageVector,
    val time: Int,
    val temp: Int
)

data class WeeklyWeatherForecast(
    val day: String,
    val icon: ImageVector,           // keep for fallback
    val lowTemp: Int,
    val highTemp: Int,
    val condition: String = "",      // e.g. "Clear Sky", "Light Rain"
    val iconUrl: String? = null      // if you have a URL from API
)