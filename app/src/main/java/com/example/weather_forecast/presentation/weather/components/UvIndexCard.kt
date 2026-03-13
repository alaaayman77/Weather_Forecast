package com.example.weather_forecast.presentation.weather.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.utils.formatNumber


@Composable
fun UvIndexCard(uvi: Double = 9.76 , language : Language) {
    val (label, color) = when {
        uvi <= 2  -> "Low"      to Color(0xFF4CAF50)
        uvi <= 5  -> "Moderate" to Color(0xFFFFC107)
        uvi <= 7  -> "High"     to Color(0xFFFF9800)
        uvi <= 10 -> "Very High" to Color(0xFFF44336)
        else      -> "Extreme"  to Color(0xFF9C27B0)
    }
    val progress = (uvi / 11f).coerceIn(0.0, 1.0).toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "UV INDEX",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${formatNumber( uvi.toInt().toString() , language)}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(color = color)
                    )
                }

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = color,
                        trackColor = color.copy(alpha = 0.15f),
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = "${formatNumber((progress * 100).toInt() , language)}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}