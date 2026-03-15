package com.example.weather_forecast.presentation.settings.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
 fun UnitChip(
    label    : String,
    selected : Boolean,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier
) {
    Surface(
        modifier      = modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable(onClick = onClick),
        shape         = RoundedCornerShape(50.dp),
        color         = if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.55f),
        border        = BorderStroke(
            width = if (selected) 0.dp else 1.dp,
            color = if (selected) Color.Transparent else Color.White.copy(alpha = 0.70f)
        )
    ) {
        Text(
            text     = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style    = MaterialTheme.typography.labelMedium.copy(
                color      = if (selected) Color.White else MaterialTheme.colorScheme.secondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}