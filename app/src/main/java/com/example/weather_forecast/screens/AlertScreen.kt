package com.example.weather_forecast.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlertScreen(modifier: Modifier){
    Box (modifier = modifier

    ){
        Text(
            text = "Alert Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
