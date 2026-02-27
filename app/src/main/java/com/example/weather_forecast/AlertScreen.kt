package com.example.weather_forecast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlertScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Alert Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
