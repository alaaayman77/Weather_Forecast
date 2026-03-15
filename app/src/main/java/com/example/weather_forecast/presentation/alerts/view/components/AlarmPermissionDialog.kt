package com.example.weather_forecast.presentation.alerts.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AlarmPermissionDialog(
    onDismiss    : () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission needed") },
        text  = {
            Text(
                "To fire alerts at exact times, allow 'Alarms & Reminders' " +
                        "in Settings → Apps → Special app access."
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onOpenSettings()
            }) { Text("Open Settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}