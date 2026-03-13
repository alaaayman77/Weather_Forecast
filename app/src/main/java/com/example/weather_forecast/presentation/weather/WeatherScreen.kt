package com.example.weather_forecast.presentation.weather

import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_forecast.data.models.CurrentWeather
import com.example.weather_forecast.data.models.DailyItem
import com.example.weather_forecast.data.models.HourlyItem
import com.example.weather_forecast.data.models.WeatherInfoItem
import com.example.weather_forecast.R
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WeeklyWeatherForecast
import com.example.weather_forecast.data.models.WindUnit


import com.example.weather_forecast.presentation.weather.components.HourlyForecastItem
import com.example.weather_forecast.presentation.weather.components.SectionCard
import com.example.weather_forecast.presentation.weather.components.SunTimesCard
import com.example.weather_forecast.presentation.weather.components.UvIndexCard
import com.example.weather_forecast.presentation.weather.components.WeatherInfoCard
import com.example.weather_forecast.presentation.weather.components.WeeklyForecastItem
import com.example.weather_forecast.ui.theme.lightGray
import com.example.weather_forecast.utils.formatNumber
import com.example.weather_forecast.utils.formatTemp
import com.example.weather_forecast.utils.formatWind
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    uiState: UiState<WeatherState>,
    location: Location?,
    tempUnit: TempUnit,
    windUnit: WindUnit,
    language: Language
) {
    Box(modifier = modifier.fillMaxSize()
          .statusBarsPadding()) {
        when (val state =uiState) {
            is UiState.Idle,
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UiState.Error -> {
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
            is UiState.Success -> {
                WeatherScreenContent(
                    location = location,
                    currentWeather     = state.data.oneCall.current,
                    hourlyList  = state.data.oneCall.hourly,
                    dailyList   = uiState.data.oneCall.daily,
                    topBarLocation = state.data.topBarLocation,
                    centerLocation = state.data.centerLocation,
                    tempUnit       = tempUnit,
                    windUnit       = windUnit,
                    language = language
                )
            }
        }
    }
}


@Composable
fun WeatherScreenContent(location: Location?, currentWeather: CurrentWeather, hourlyList: List<HourlyItem>, dailyList: List<DailyItem> ,  topBarLocation: String , centerLocation: String,   tempUnit: TempUnit,      // ← add
                         windUnit: WindUnit, language: Language ) {
    val context  = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 0.dp)
    ) {
        item { WeatherTopBar(currentWeather ,  topBarLocation ) }
        item { WeatherCenterSection(currentWeather , centerLocation, tempUnit , language) }
        item { WeatherInfoGrid(currentWeather ,tempUnit, windUnit , language) }
        item {
            SectionCard {
                HourlyForecastList(hourlyList = hourlyList, tempUnit,language)
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
            UvIndexCard(uvi = currentWeather.uvi , language = language )
        }
        item {
            SectionCard {
              WeeklyList(dailyList = dailyList , tempUnit , language)
            }
        }




    }
}
@Composable
fun WeeklyList(dailyList: List<DailyItem> , tempUnit: TempUnit , language: Language) {
    val days      = dailyList.take(7)
    val globalMin = days.minOfOrNull { UnitConverter.convertTemp(it.temp.min, tempUnit)  } ?: 0
    val globalMax = days.maxOfOrNull { UnitConverter.convertTemp(it.temp.max, tempUnit)} ?: 40

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
                    lowTemp   = UnitConverter.convertTemp(day.temp.min, tempUnit),
                    highTemp  = UnitConverter.convertTemp(day.temp.max, tempUnit),
                    condition = day.weather.firstOrNull()?.description
                        ?.replaceFirstChar { it.uppercase() } ?: "",
                    iconUrl   = day.weather.firstOrNull()?.iconUrl()
                ),
                tempUnit = tempUnit,
                globalMin = globalMin,
                globalMax = globalMax,
                language = language
            )
        }
    }
}
@Composable
fun WeatherTopBar(currentWeather: CurrentWeather, topBarLocation: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = getGreeting(currentWeather.dt),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = formatDate(currentWeather.dt),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        )
                    )
                }


                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(lightGray.copy(alpha = 0.5f))
                )


                Text(
                    text = formatTime(currentWeather.dt),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = lightGray,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }


        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
            modifier = Modifier.widthIn(max = 155.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = topBarLocation,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WeatherCenterSection(currentWeather: CurrentWeather , centerLocation: String ,  tempUnit: TempUnit,language: Language ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = centerLocation,
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
            text = "${formatTemp(UnitConverter.convertTemp(currentWeather.temp, tempUnit), tempUnit , language)}",
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 72.sp ,
                textDirection = TextDirection.Ltr
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
                value = "${formatTemp(UnitConverter.convertTemp(currentWeather.temp, tempUnit),tempUnit ,language)}",
                isHigh = true
            )
            TempPill(
                label = "L",
                value = "${formatTemp(UnitConverter.convertTemp(currentWeather.dew_point, tempUnit), tempUnit,language)}",
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
                text = "${stringResource(R.string.feels_like) }${formatTemp(UnitConverter.convertTemp(currentWeather.temp, tempUnit), tempUnit , language)}"
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
                    fontWeight = FontWeight.Bold,
                    textDirection = TextDirection.Ltr
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
            painter = painterResource(R.drawable.thermometer),
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
fun WeatherInfoGrid(currentWeather: CurrentWeather, tempUnit: TempUnit , windUnit: WindUnit , language: Language) {
    val row1 = listOf(
        WeatherInfoItem(R.drawable.ic_humid,    "${formatNumber(currentWeather.humidity , language)}%",      stringResource(R.string.humid)),
        WeatherInfoItem(R.drawable.ic_wind,     "${formatWind(currentWeather.wind_speed, windUnit, language)}",stringResource(R.string.wind)),
        WeatherInfoItem(R.drawable.ic_pressure, "${formatNumber(currentWeather.pressure , language)} hPa",   stringResource(R.string.pressure)),
    )
    val row2 = listOf(
        WeatherInfoItem(R.drawable.ic_cloud,   "${formatNumber(currentWeather.clouds , language)}%",stringResource(R.string.cloud)),
        WeatherInfoItem(R.drawable.ic_sunrise, formatTime(currentWeather.sunrise),      stringResource(R.string.sunrise)),
        WeatherInfoItem(R.drawable.ic_sunset,  formatTime(currentWeather.sunset),       stringResource(R.string.sunset)),
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherStatRow(row1)
        WeatherStatRow(row2)
    }
}


@Composable
private fun WeatherStatRow(stats: List<WeatherInfoItem>) {
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
fun HourlyForecastList(hourlyList: List<HourlyItem>, tempUnit: TempUnit , language: Language) {
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
                    isNow      = index == 0,
                    tempUnit = tempUnit,
                    language= language
                )
            }
        }
    }
}




fun formatTime(timestamp: Long?): String {
    if (timestamp == null) return "--"
    return SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(timestamp * 1000))
}

fun formatDate(timestamp: Long?): String {
    if (timestamp == null) return "--"
    return SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        .format(Date(timestamp * 1000))
}


fun formatDateTime(timestamp: Long?): String {
    if (timestamp == null) return "--"
    return SimpleDateFormat("EEEE, dd MMM  •  hh:mm a", Locale.getDefault())
        .format(Date(timestamp * 1000))
}

fun getGreeting(timestamp: Long?): String = when (hourOf(timestamp)) {
    in 5..11  -> "Good Morning"
    in 12..16 -> "Good Afternoon"
    in 17..20 -> "Good Evening"
    else      -> "Good Night"
}

private fun hourOf(timestamp: Long?): Int {
    if (timestamp == null) return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return Calendar.getInstance().apply { time = Date(timestamp * 1000) }
        .get(Calendar.HOUR_OF_DAY)
}
