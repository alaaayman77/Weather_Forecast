package com.example.weather_forecast.presentation.favouriteDetails.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit

import com.example.weather_forecast.presentation.WeatherState


import com.example.weather_forecast.presentation.UiState
import com.example.weather_forecast.presentation.weather.view.WeatherScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteDetailsScreen(
    modifier: Modifier = Modifier,
    lat: Double,
    lon: Double,
    onBack: () -> Unit,
    uiState: UiState<WeatherState>,
    tempUnit: TempUnit,
    windUnit: WindUnit,
    language: Language
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Star,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text  = "Favourites",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->

        WeatherScreen(
            modifier = modifier.padding(innerPadding),
            uiState  = uiState,
            location = null,
            tempUnit = tempUnit,
            windUnit =windUnit,
            language = language
        )
    }
}