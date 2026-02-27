package com.example.weather_forecast.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.R


@Composable
fun HomeScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),

    ){
        WeatherDetailUpperPart()
    }
}

@Composable
fun WeatherDetailUpperPart(){
    Column(
        modifier = Modifier

            .fillMaxWidth()
            .padding(start = 10.dp),
        verticalArrangement = Arrangement.SpaceEvenly

    ) {
        Text("Good Morning", style = MaterialTheme.typography.labelSmall)
        Text("Thu 31 Dec", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )

    }
}

@Composable
@Preview(showSystemUi = true )
fun WeatherDetailUpperPartPreview(){
    WeatherDetailUpperPart()
}