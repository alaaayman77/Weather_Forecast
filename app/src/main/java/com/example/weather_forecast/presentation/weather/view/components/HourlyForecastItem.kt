package com.example.weather_forecast.presentation.weather.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weather_forecast.R
import com.example.weather_forecast.data.models.HourlyItem
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.utils.formatNumber
import com.example.weather_forecast.utils.formatTemp


@Composable
fun HourlyForecastItem(hourlyItem: HourlyItem, isNow: Boolean = false, tempUnit: TempUnit , language: Language) {
    val containerColor = if (isNow)
        MaterialTheme.colorScheme.primary
    else
        Color.White.copy(alpha = 0.4f)

    val textColor = if (isNow)
        Color.White
    else
        MaterialTheme.colorScheme.secondary

    val borderColor = if (isNow)
        MaterialTheme.colorScheme.primary
    else
        Color.White.copy(alpha = 0.6f)

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isNow) stringResource(R.string.now)else formatNumber(hourlyItem.hour(),language),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,

                    fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal,

                )
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isNow) Color.White.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                hourlyItem.weather.firstOrNull()?.iconUrl()?.let { iconUrl ->
                    AsyncImage(
                        model = iconUrl,
                        contentDescription = "Weather icon",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Text(
                text = "${formatTemp(UnitConverter.convertTemp(hourlyItem.temp, tempUnit), tempUnit,language)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    textDirection = TextDirection.Ltr

                )
            )
        }
    }
}