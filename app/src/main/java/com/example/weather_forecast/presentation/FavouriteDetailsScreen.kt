package com.example.weather_forecast.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FavouriteDetailsScreen(
    modifier: Modifier,
    lat: Double,
    lon: Double
) {
    Box(modifier = modifier) {
        Text(
            text  = "Favourite Details Screen\n$lat, $lon",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}