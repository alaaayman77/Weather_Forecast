package com.example.weather_forecast.presentation.alerts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.models.AlertItem
import com.example.weather_forecast.data.models.WeatherAlert
import com.example.weather_forecast.presentation.weather.AlertState
import com.example.weather_forecast.presentation.weather.UiState


@Composable
 fun ActiveAlertsContent(
    weatherAlertsState: UiState<AlertState>,
    scheduledAlerts   : List<AlertItem>,
    onCancelScheduled : (AlertItem) -> Unit,
    onRetry           : () -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (scheduledAlerts.isEmpty()) {
            item {
                EmptyStateContent(
                    message = "Your past alerts will appear here once\nyou've set up and triggered alerts."
                )
            }
        } else {
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader("Scheduled Alerts", Color(0xFF1E88E5))
            }
            items(scheduledAlerts, key = { it.id }) { item ->
                ScheduledAlertCard(
                    item = item,
                    onCancel= { onCancelScheduled(item) }
                )
            }
        }
    }
}
