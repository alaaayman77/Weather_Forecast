package com.example.weather_forecast.presentation.alerts.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.ui.theme.lightGray

@Composable
 fun AlertTypeCard(
    type: AlertType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor     = if (isSelected) type.color.copy(alpha = 0.12f) else Color.White
    val borderColor = if (isSelected) type.color else lightGray
    val iconTint    = if (isSelected) type.color else lightGray
    val textColor   = if (isSelected) Color.Black else lightGray

    Surface(
        modifier  = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        color = bgColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector     = type.icon,
                contentDescription = null,
                tint            = iconTint,
                modifier        = Modifier.size(26.dp)
            )
            Text(
                text  = type.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color      = textColor
                )
            )
            Text(
                text  = type.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color    = lightGray
                )
            )
        }
    }
}


