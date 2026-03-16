package com.example.weather_forecast.data.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.weather_forecast.R

enum class AlertType(
    @StringRes val labelRes   : Int,
    @StringRes val subtitleRes: Int,
    val icon    : ImageVector,
    val color   : Color
) {
    NOTIFICATION(R.string.notification, R.string.notification_subtitle, Icons.Default.Notifications, Color(0xFF42A5F5)),
    ALERT       (R.string.alert,        R.string.alert_subtitle, Icons.Default.Warning,   Color(0xFFFF7043))
}

 enum class AlertTab(val label: String, val icon: ImageVector) {
    ACTIVE ("Active",  Icons.Default.Notifications),
    HISTORY("History", Icons.Default.Home)
}