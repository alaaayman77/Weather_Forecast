package com.example.weather_forecast.presentation.settings.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.ui.theme.lightGray


@Composable
fun LocationOptionCard(
    title    : String,
    subtitle : String,
    selected : Boolean,
    onClick  : () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.70f)
    val borderWidth = if (selected) 1.5.dp else 1.dp

    Surface(
        modifier      = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape         = RoundedCornerShape(14.dp),
        color         = Color.White.copy(alpha = 0.55f),
        border        = BorderStroke(borderWidth, borderColor),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text  = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color      = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(color = lightGray)
                )
            }
            RadioButton(
                selected = selected,
                onClick  = onClick,
                colors   = RadioButtonDefaults.colors(
                    selectedColor   = MaterialTheme.colorScheme.primary,
                    unselectedColor = lightGray.copy(alpha = 0.5f)
                )
            )
        }
    }
}
