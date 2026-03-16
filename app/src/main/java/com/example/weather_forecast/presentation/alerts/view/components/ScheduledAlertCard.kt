package com.example.weather_forecast.presentation.alerts.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.ui.theme.lightGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.weather_forecast.R

@Composable
fun ScheduledAlertCard(
    item    : AlertEntity,
    onCancel: () -> Unit
) {
    val statusText  = when (item.status) {
        AlertStatus.SCHEDULED -> stringResource(R.string.alert_scheduled)
        AlertStatus.ACTIVE    -> stringResource(R.string.alert_active)
        AlertStatus.DISMISSED -> stringResource(R.string.alert_dismissed)
        AlertStatus.CANCELLED ->  stringResource(R.string.alert_cancelled)
    }
    val statusColor = when (item.status) {
        AlertStatus.SCHEDULED -> Color(0xFF1E88E5)
        AlertStatus.ACTIVE    -> Color(0xFF43A047)
        AlertStatus.DISMISSED -> Color(0xFFFFA000)
        AlertStatus.CANCELLED -> Color(0xFFE53935)
    }
    val layoutDirection = LocalLayoutDirection.current

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
                        if (item.mode == AlertMode.CUSTOM)  stringResource(R.string.condition_based_alert)
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


            if (item.mode == AlertMode.CUSTOM && item.customCondition != null) {
                Surface(
                    shape    = RoundedCornerShape(8.dp),
                    color    = Color(0xFFE3F2FD),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text     = "${stringResource(R.string.watching)} ${item.customCondition.emoji} ${item.customCondition.label}",
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
                    label    = if (item.mode == AlertMode.CUSTOM) stringResource(R.string.from) else  stringResource(R.string.start),
                    time     = formatMillis(item.startMillis),
                    color    = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = if (layoutDirection == LayoutDirection.Rtl)
                        Icons.Default.ArrowBack
                    else
                        Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint               = lightGray,
                    modifier           = Modifier.size(14.dp)
                )
                TimeChip(
                    label    = if (item.mode == AlertMode.CUSTOM) stringResource(R.string.until) else  stringResource(R.string.end),
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
                            stringResource(R.string.duration),
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
    SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(millis))

private fun durationLabel(startMillis: Long, endMillis: Long): String {
    val diffMinutes = ((endMillis - startMillis) / 1000 / 60).toInt()
    return when {
        diffMinutes < 60          -> "${diffMinutes}m"
        diffMinutes % 60 == 0     -> "${diffMinutes / 60}h"
        else                      -> "${diffMinutes / 60}h ${diffMinutes % 60}m"
    }
}