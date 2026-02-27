package com.example.weather_forecast.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.ui.theme.lightGray

data class WeatherStat(
    val icon: ImageVector,
    val value: String,
    val label: String
)

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        WeatherMainScreen()
    }
}

@Composable
fun WeatherMainScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            WeatherTopBar()
        }

        item {
            WeatherCenterSection()
        }
        item {
            WeatherInfoGrid()
        }
    }
}



@Composable
fun WeatherTopBar() {
    Column {
        Text(
            text = "Good Morning",
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = "Monday, 12 June  •  10:30 AM",
            style = MaterialTheme.typography.labelSmall.copy(color = lightGray),

            )
    }
}



@Composable
fun WeatherCenterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text("Montreal", style = MaterialTheme.typography.headlineLarge.copy(color = Color.White , letterSpacing = 0.5.sp))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "24°C",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary , fontSize = 72.sp)

        )

        Text(
            text = "Partly Cloudy",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

val stats = listOf(
    WeatherStat(Icons.Default.Home, "64%", "HUMID"),
    WeatherStat(Icons.Default.Home, "12km/h", "WIND"),
    WeatherStat(Icons.Default.Home, "1012", "HPA"),
    WeatherStat(Icons.Default.Home, "28%", "CLOUD"),
    WeatherStat(Icons.Default.Home, "06:12", "SUNRISE"),
    WeatherStat(Icons.Default.Home, "18:45", "SUNSET"),
)
@Composable
fun WeatherInfoGrid() {
    val row1 = listOf(
        WeatherStat(Icons.Default.Home, "64%",    "HUMID"),
        WeatherStat(Icons.Default.Home,       "12km/h", "WIND"),
        WeatherStat(Icons.Default.Home,     "1012",   "HPA"),
    )
    val row2 = listOf(
        WeatherStat(Icons.Default.Home,      "28%",   "CLOUD"),
        WeatherStat(Icons.Default.Home,   "06:12",  "SUNRISE"),
        WeatherStat(Icons.Default.Home, "18:45", "SUNSET"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherStatRow(row1)
        WeatherStatRow(row2)
    }
}
@Composable
private fun WeatherStatRow(stats: List<WeatherStat>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            WeatherInfoCard(modifier = Modifier.weight(1f), stat = stat)
        }
    }
}


@Composable
fun WeatherInfoCard(

    stat: WeatherStat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = stat.label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = stat.value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )

            Text(
                text = stat.label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}