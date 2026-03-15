package com.example.weather_forecast.presentation.alerts.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.R
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.presentation.AlertState
import com.example.weather_forecast.presentation.UiState


@Composable
fun ActiveAlertsContent(
    weatherAlertsState: UiState<AlertState>,
    scheduledAlerts   : List<AlertEntity>,
    alertStatuses     : Map<Int, AlertStatus>,
    onCancelScheduled : (AlertEntity) -> Unit,
    onRetry           : () -> Unit,

) {
    if (scheduledAlerts.isEmpty()) {
        EmptyStateContent(
            message = stringResource(R.string.no_alerts),

        )
    } else {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader(stringResource(R.string.scheduled_alerts))
            }
            items(scheduledAlerts, key = { it.id }) { item ->
                ScheduledAlertCard(
                    item     = item,
                    onCancel = { onCancelScheduled(item) }
                )
            }
        }
    }
}