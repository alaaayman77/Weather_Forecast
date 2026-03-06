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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_forecast.data.models.CurrentWeather
import com.example.weather_forecast.data.models.DailyItem
import com.example.weather_forecast.data.models.HourlyItem
import com.example.weather_forecast.data.models.HourlyWeatherStat

import com.example.weather_forecast.data.models.WeatherStat
import com.example.weather_forecast.data.models.WeeklyWeatherForecast
import com.example.weather_forecast.presentation.weather.components.HourlyForecastItem
import com.example.weather_forecast.presentation.weather.components.SectionCard
import com.example.weather_forecast.presentation.weather.components.SunTimesCard
import com.example.weather_forecast.presentation.weather.components.UvIndexCard
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
                    currentWeather     = uiState.data.current,
                    hourlyList  = uiState.data.hourly,
                    dailyList   = uiState.data.daily,
                    timezone      = uiState.data.timezone
                )
            }
        }
    }
}


@Composable
fun WeatherScreenContent(location: Location?, currentWeather: CurrentWeather, hourlyList: List<HourlyItem>, dailyList: List<DailyItem> ,  timezone: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 0.dp)
    ) {
        item { WeatherTopBar(currentWeather , timezone) }
        item { WeatherCenterSection(currentWeather) }
        item { WeatherInfoGrid(currentWeather) }
        item {
            SectionCard {
                HourlyForecastList(hourlyList = hourlyList)
            }
        }
        item {
            SunTimesCard(
                sunriseText = formatTime(currentWeather.sunrise),
                sunsetText  = formatTime(currentWeather.sunset),
                progress    = run {
                    val now = System.currentTimeMillis() / 1000
                    ((now - currentWeather.sunrise).toFloat() /
                            (currentWeather.sunset - currentWeather.sunrise))
                        .coerceIn(0f, 1f)
                }
            )
        }
        item {
            UvIndexCard(uvi = currentWeather.uvi)
        }
        item {
            SectionCard {
              WeeklyList(dailyList = dailyList)
            }
        }




    }
}
@Composable
fun WeeklyList(dailyList: List<DailyItem>) {
    val days      = dailyList.take(7)
    val globalMin = days.minOfOrNull { it.temp.min.toCelsius() } ?: 0
    val globalMax = days.maxOfOrNull { it.temp.max.toCelsius() } ?: 40

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "7-DAY FORECAST",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        days.forEachIndexed { index, day ->
            WeeklyForecastItem(
                weeklyWeatherForecast = WeeklyWeatherForecast(
                    day       = if (index == 0) "Today" else day.dayName(),
                    icon      = Icons.Default.Home,
                    lowTemp   = day.temp.min.toCelsius(),
                    highTemp  = day.temp.max.toCelsius(),
                    condition = day.weather.firstOrNull()?.description
                        ?.replaceFirstChar { it.uppercase() } ?: "",
                    iconUrl   = day.weather.firstOrNull()?.iconUrl()
                ),
                globalMin = globalMin,
                globalMax = globalMax
            )
        }
    }
}
@Composable
fun WeatherTopBar(currentWeather: CurrentWeather , timezone: String) {
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
            val locationLabel = timezone.substringAfterLast("/").replace("_", " ")
            Text(
                text = "📍 ${locationLabel}",
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
fun WeatherCenterSection(currentWeather: CurrentWeather) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = currentWeather.weather.firstOrNull()?.main ?: "",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary
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
            text = "${currentWeather.temp.toCelsius()}°",
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 72.sp
            )
        )

        Text(
            text = currentWeather.weather.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() } ?: "",
            style = MaterialTheme.typography.titleMedium.copy(
                color = lightGray,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TempPill(
                label = "H",
                value = "${currentWeather.temp.toCelsius()}°",
                isHigh = true
            )
            TempPill(
                label = "L",
                value = "${currentWeather.dew_point.toCelsius()}°",
                isHigh = false
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            WeatherDetailChip(
                icon = Icons.Default.Home,
                text = "Feels like ${currentWeather.feels_like.toCelsius()}°"
            )
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
fun WeatherInfoGrid(currentWeather: CurrentWeather) {
    val row1 = listOf(
        WeatherStat(Icons.Default.Home, "${currentWeather.humidity}%",          "HUMID"),
        WeatherStat(Icons.Default.Home,       "${currentWeather.wind_speed} m/s",     "WIND"),
        WeatherStat(Icons.Default.Home,     "${currentWeather.pressure} hPa",       "PRESSURE"),
    )
    val row2 = listOf(
        WeatherStat(Icons.Default.Home,     "${currentWeather.clouds}%",            "CLOUD"),
        WeatherStat(Icons.Default.Home,   formatTime(currentWeather.sunrise),     "SUNRISE"),
        WeatherStat(Icons.Default.Home, formatTime(currentWeather.sunset),     "SUNSET"),
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
    WeeklyWeatherForecast("Today", Icons.Default.Home,  16, 28, "Clear Sky"),
    WeeklyWeatherForecast("Sat",   Icons.Default.Home,    14, 26, "Partly Cloudy"),
    WeeklyWeatherForecast("Sun",   Icons.Default.Home,    13, 21, "Light Rain"),
    WeeklyWeatherForecast("Mon",   Icons.Default.Home, 12, 18, "Thunderstorm"),
    WeeklyWeatherForecast("Tue",   Icons.Default.Home,    14, 23, "Cloudy"),
)

@Composable
fun HourlyForecastList(hourlyList: List<HourlyItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "HOURLY FORECAST",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(hourlyList.size) { index ->
                HourlyForecastItem(
                    hourlyItem = hourlyList[index],
                    isNow      = index == 0
                )
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

fun Double.toCelsius() = (this - 273.15).toInt()