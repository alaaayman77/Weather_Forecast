package com.example.weather_forecast.presentation.weather

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_forecast.data.models.HourlyForecastResponse
import com.example.weather_forecast.data.models.HourlyItem
import com.example.weather_forecast.data.models.HourlyWeatherStat
import com.example.weather_forecast.data.models.WeatherResponse
import com.example.weather_forecast.data.models.WeatherStat
import com.example.weather_forecast.data.models.WeeklyWeatherForecast
import com.example.weather_forecast.presentation.weather.components.HourlyForecastItem
import com.example.weather_forecast.presentation.weather.components.WeatherInfoCard
import com.example.weather_forecast.presentation.weather.components.WeeklyForecastItem
import com.example.weather_forecast.ui.theme.lightGray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    uiState: WeatherUiState,
    location: Location?,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is WeatherUiState.Idle,
            is WeatherUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is WeatherUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodySmall.copy(color = lightGray)
                    )
                }
            }
            is WeatherUiState.Success -> {
                WeatherScreenContent(
                    location = location,
                    currentWeather = uiState.weather,
                    hourlyList     = uiState.hourly
                )
            }
        }
    }
}


@Composable
fun WeatherScreenContent(location: Location?, currentWeather: WeatherResponse, hourlyList : List<HourlyItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item { WeatherTopBar(currentWeather) }
        item { WeatherCenterSection(currentWeather) }
        item { WeatherInfoGrid(currentWeather) }
        item { HourlyForecastList( hourlyList = hourlyList) }
        item {
            Text(
                text = "Weekly Forecast",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        items(weeklyStats.size) { index ->
            WeeklyForecastItem(weeklyStats[index])
        }
    }
}

@Composable
fun WeatherTopBar(currentWeather: WeatherResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = getGreeting(currentWeather.dt),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            Text(
                text = formatDateTime(currentWeather.dt),
                style = MaterialTheme.typography.bodySmall.copy(color = lightGray)
            )
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Text(
                text = "📍 ${currentWeather.sys.country}",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun WeatherCenterSection(currentWeather: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = currentWeather.name,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        )
        currentWeather.weather.firstOrNull()?.iconUrl()?.let { iconUrl ->
            AsyncImage(
                model = iconUrl,
                contentDescription = "Weather icon",
                modifier = Modifier.size(100.dp)
            )
        }

        Text(
            text = "${currentWeather.main.temp.toInt()}°",
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 72.sp
            )
        )

        Text(
            text = currentWeather.weather.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() } ?: "",
            style = MaterialTheme.typography.labelMedium.copy(
                color = lightGray,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TempPill(
                label = "H",
                value = "${currentWeather.main.temp_max.toInt()}°",
                isHigh = true
            )
            TempPill(
                label = "L",
                value = "${currentWeather.main.temp_min.toInt()}°",
                isHigh = false
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherDetailChip(
                icon = Icons.Default.Person,
                text = "Feels like ${currentWeather.main.feels_like.toInt()}°"
            )
            WeatherDetailChip(
                icon = Icons.Default.Home,
                text = "${currentWeather.visibility / 1000} km"
            )
            currentWeather.rain?.`1h`?.let { rain ->
                WeatherDetailChip(
                    icon = Icons.Default.Home,
                    text = "${rain}mm/h"
                )
            }
        }
    }
}

@Composable
fun TempPill(label: String, value: String, isHigh: Boolean) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isHigh) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isHigh) "High" else "Low",
                tint = if (isHigh) MaterialTheme.colorScheme.primary else lightGray,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$label: $value",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (isHigh) MaterialTheme.colorScheme.primary else lightGray,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun WeatherDetailChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = lightGray,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(color = lightGray)
        )
    }
}

@Composable
fun WeatherInfoGrid(currentWeather: WeatherResponse) {
    val row1 = listOf(
        WeatherStat(Icons.Default.Home, "${currentWeather.main.humidity}%", "HUMID"),
        WeatherStat(Icons.Default.Home, "${currentWeather.wind.speed} m/s", "WIND"),
        WeatherStat(Icons.Default.Home, "${currentWeather.main.pressure}", "HPA"),
    )
    val row2 = listOf(
        WeatherStat(Icons.Default.Home, "${currentWeather.clouds.all}%", "CLOUD"),
        WeatherStat(Icons.Default.Home, formatTime(currentWeather.sys.sunrise), "SUNRISE"),
        WeatherStat(Icons.Default.Home, formatTime(currentWeather.sys.sunset), "SUNSET"),
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

val stats = listOf(
    HourlyWeatherStat(Icons.Default.Home, 10, 22),
    HourlyWeatherStat(Icons.Default.Home, 11, 21),
    HourlyWeatherStat(Icons.Default.Home, 12, 20),
    HourlyWeatherStat(Icons.Default.Home, 13, 23),
    HourlyWeatherStat(Icons.Default.Home, 14, 22),
)

val weeklyStats = listOf(
    WeeklyWeatherForecast("Mon", Icons.Default.Home, 18, 24),
    WeeklyWeatherForecast("Tue", Icons.Default.Home, 16, 22),
    WeeklyWeatherForecast("Wed", Icons.Default.Home, 14, 20),
    WeeklyWeatherForecast("Thu", Icons.Default.Home, 17, 25),
    WeeklyWeatherForecast("Fri", Icons.Default.Home, 15, 21),
)

@Composable
fun HourlyForecastList( hourlyList : List<HourlyItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hourly Forecast",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hourlyList.size) { item ->
                HourlyForecastItem(hourlyList[item])
            }
        }
    }
}

fun formatTime(timestamp: Long?): String {
    if (timestamp == null) return "--"
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun formatDateTime(timestamp: Long?): String {
    if (timestamp == null) return "--"
    val sdf = SimpleDateFormat("EEEE, dd MMM  •  hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun getGreeting(timestamp: Long?): String {
    val hour = if (timestamp != null) {
        val cal = Calendar.getInstance()
        cal.time = Date(timestamp * 1000)
        cal.get(Calendar.HOUR_OF_DAY)
    } else {
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }
    return when (hour) {
        in 5..11  -> "Good Morning ☀️"
        in 12..16 -> "Good Afternoon 🌤️"
        in 17..20 -> "Good Evening 🌆"
        else      -> "Good Night 🌙"
    }
}