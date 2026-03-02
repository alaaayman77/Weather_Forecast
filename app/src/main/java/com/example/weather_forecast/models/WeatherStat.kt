package com.example.weather_forecast.models

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
    val day : String,
    val icon : ImageVector,
    val highTemp : Int,
    val lowTemp: Int
)