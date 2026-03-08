package com.example.weather_forecast.data.models

import kotlinx.serialization.Serializable


@Serializable
data class DailyItem(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Double,
    val summary: String,
    val temp: DailyTemp,
    val feels_like: DailyFeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double?,
    val weather: List<WeatherCondition>,
    val clouds: Int,
    val pop: Double,
    val uvi: Double
) {
    fun dayName(): String {
        val sdf = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(dt * 1000))
    }
}
@Serializable
data class DailyTemp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)
@Serializable
data class DailyFeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

