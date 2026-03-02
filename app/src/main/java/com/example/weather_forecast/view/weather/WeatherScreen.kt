package com.example.weather_forecast.view.weather

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.models.HourlyWeatherStat
import com.example.weather_forecast.models.WeatherStat
import com.example.weather_forecast.ui.theme.lightGray
import com.example.weather_forecast.view.weather.components.HourlyForecastItem
import com.example.weather_forecast.view.weather.components.WeatherInfoCard


@Composable
fun WeatherScreen(modifier: Modifier = Modifier , location: Location) {
    Box(
        modifier = modifier
    ) {
        WeatherScreenContent()
    }
}
@Composable
fun WeatherScreenContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
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
        item {
            hourlyForecastList()
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

        Text("Montreal", style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary , letterSpacing = 0.5.sp))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "24°C",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary , fontSize = 72.sp)

        )

        Text(
            text = "Partly Cloudy",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

val stats  = listOf(
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
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




//@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {

    }
}
@Composable
fun hourlyForecastList(){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Hourly Forecast",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )
    LazyRow(modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){

        items(
            stats.size,

        ){item->
            HourlyForecastItem(stats.get(item))
        }

    }
    }
}

@Preview(showBackground = true)
@Composable
fun hourlyForecastPreview(){
    hourlyForecastList()
}

@Preview(showBackground = true)
@Composable
fun hourlyForecastItemPreview(){
HourlyForecastItem(hourlyWeatherStat = HourlyWeatherStat(Icons.Default.Home , 10 , 22))
}
