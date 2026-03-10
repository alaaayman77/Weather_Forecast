package com.example.weather_forecast.presentation.alerts.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
 fun SectionHeader(title: String, color: Color) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.labelLarge.copy(
            color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
