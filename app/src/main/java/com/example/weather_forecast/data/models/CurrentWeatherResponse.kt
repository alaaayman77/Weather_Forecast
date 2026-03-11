package com.example.weather_forecast.data.models

import kotlinx.serialization.Serializable


data class OneCallResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeather,
    val hourly: List<HourlyItem>,
    val daily: List<DailyItem>,
    val alerts: List<WeatherAlert>  = emptyList()
)
@Serializable
data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double?,
    val weather: List<WeatherCondition>
)




@Serializable
data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) {
    fun iconUrl() = "https://openweathermap.org/img/wn/${icon}@2x.png"
}