package com.example.weather_forecast.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(modifier: Modifier){
    Box (modifier = modifier

    ){
        Text(
            text = "Settings Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
