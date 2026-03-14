package com.example.weather_forecast.presentation.alerts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.ui.theme.lightGray

@Composable
fun ScheduledAlertCard(
    item    : AlertEntity,
    onCancel: () -> Unit
) {
    val statusText  = when (item.status) {
        AlertStatus.SCHEDULED -> "Scheduled"
        AlertStatus.ACTIVE    -> "Active"
        AlertStatus.DISMISSED -> "Dismissed"
        AlertStatus.CANCELLED -> "Cancelled"
    }
    val statusColor = when (item.status) {
        AlertStatus.SCHEDULED -> Color(0xFF1E88E5)
        AlertStatus.ACTIVE    -> Color(0xFF43A047)
        AlertStatus.DISMISSED -> Color(0xFFFFA000)
        AlertStatus.CANCELLED -> Color(0xFFE53935)
    }

    Surface(
        shape           = RoundedCornerShape(20.dp),
        color           = Color.White.copy(alpha = 0.92f),
        shadowElevation = 6.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(item.type.color.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        item.type.icon, null,
                        tint     = item.type.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource( item.type.labelRes),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF0D2B4E)
                        )
                    )
                    Text(
                        if (item.mode == AlertMode.CUSTOM) "Condition-based alert"
                        else stringResource( item.type.subtitleRes),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color    = lightGray,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            color      = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 10.sp
                        )
                    )
                }

                IconButton(onClick = onCancel, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Delete, "Cancel",
                        tint     = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE3F2FD), thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // Custom condition badge
            if (item.mode == AlertMode.CUSTOM && item.customCondition != null) {
                Surface(
                    shape    = RoundedCornerShape(8.dp),
                    color    = Color(0xFFE3F2FD),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text     = "Watching: ${item.customCondition.emoji} ${item.customCondition.label}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            color      = Color(0xFF1565C0),
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 11.sp
                        )
                    )
                }
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                TimeChip(
                    label    = if (item.mode == AlertMode.CUSTOM) "From" else "Start",
                    time     = formatMillis(item.startMillis),
                    color    = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowForward, null,
                    tint     = lightGray,
                    modifier = Modifier.size(14.dp)
                )
                TimeChip(
                    label    = if (item.mode == AlertMode.CUSTOM) "Until" else "End",
                    time     = formatMillis(item.endMillis),
                    color    = Color(0xFF43A047),
                    modifier = Modifier.weight(1f)
                )
                Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF0F6FF)) {
                    Column(
                        modifier            = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Duration",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color    = lightGray,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            durationLabel(item.startMillis, item.endMillis),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color      = Color(0xFF0D2B4E),
                                fontWeight = FontWeight.Bold,
                                fontSize   = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeChip(
    label   : String,
    time    : String,
    color   : Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape    = RoundedCornerShape(10.dp),
        color    = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color      = color,
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                time,
                style = MaterialTheme.typography.labelMedium.copy(
                    color      = Color(0xFF0D2B4E),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 12.sp
                )
            )
        }
    }
}

private fun formatMillis(millis: Long): String =
    java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        .format(java.util.Date(millis))

private fun durationLabel(startMillis: Long, endMillis: Long): String {
    val diffMinutes = ((endMillis - startMillis) / 1000 / 60).toInt()
    return when {
        diffMinutes < 60          -> "${diffMinutes}m"
        diffMinutes % 60 == 0     -> "${diffMinutes / 60}h"
        else                      -> "${diffMinutes / 60}h ${diffMinutes % 60}m"
    }
}