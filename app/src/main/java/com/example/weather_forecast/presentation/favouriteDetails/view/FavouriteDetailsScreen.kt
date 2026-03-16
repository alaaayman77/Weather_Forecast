package com.example.weather_forecast.presentation.favouriteDetails.view

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import com.example.weather_forecast.presentation.WeatherState
import com.example.weather_forecast.presentation.UiState
import com.example.weather_forecast.presentation.weather.view.WeatherScreen
import com.example.weather_forecast.R

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
    language: Language,
    isOffline   : Boolean,
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
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Rounded.Favorite,
                                contentDescription = null,
                                tint               = Color.Red,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text  = stringResource(R.string.favourite),
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
            language = language,
            isOffline = isOffline
        )
    }
}