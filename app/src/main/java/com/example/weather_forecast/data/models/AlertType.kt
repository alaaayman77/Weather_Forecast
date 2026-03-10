package com.example.weather_forecast.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AlertType(
    val label   : String,
    val subtitle: String,
    val icon    : ImageVector,
    val color   : Color
) {
    NOTIFICATION("Notification", "Silent push alert", Icons.Default.Home, Color(0xFF42A5F5)),
    ALERT       ("Alert",        "Sound + vibration", Icons.Default.Notifications,   Color(0xFFFF7043))
}

 enum class AlertTab(val label: String, val icon: ImageVector) {
    ACTIVE ("Active",  Icons.Default.Notifications),
    HISTORY("History", Icons.Default.Home)
}