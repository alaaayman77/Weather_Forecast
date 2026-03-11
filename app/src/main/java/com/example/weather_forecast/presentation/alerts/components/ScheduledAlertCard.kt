package com.example.weather_forecast.presentation.alerts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.models.AlertItem
import com.example.weather_forecast.ui.theme.lightGray

@Composable
fun ScheduledAlertCard(item: AlertItem, onCancel: () -> Unit) {
    Surface(
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White.copy(alpha = 0.85f),
        shadowElevation = 4.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.type.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.type.icon, null, tint = item.type.color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.type.label, style = MaterialTheme.typography.labelLarge.copy(
                 color = Color.Black))
                Text(item.label, style = MaterialTheme.typography.bodySmall.copy(
                    color = lightGray
                ))
            }
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Delete, "Cancel alert", tint = Color.Red)
            }
        }
    }
}


