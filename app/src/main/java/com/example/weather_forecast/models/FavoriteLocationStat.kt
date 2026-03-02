package com.example.weather_forecast.models

import androidx.compose.ui.graphics.vector.ImageVector

data class FavoriteLocationStat(
    val icon: ImageVector,
    val locationName: String,
    val highTemp : Int,
    val weatherState: String
)