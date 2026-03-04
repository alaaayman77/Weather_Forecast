package com.example.weather_forecast.data.models

data class WeatherResponse(
    val coord: Coord,
    val weather: List<WeatherDescription>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int?,
    val grnd_level: Int?
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)

data class Rain(
    val `1h`: Double?
)

data class Clouds(
    val all: Int
)

data class Sys(
    val country: String?,
    val sunrise: Long,
    val sunset: Long
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) {

    fun iconUrl(): String = "https://openweathermap.org/img/wn/${icon}@2x.png"
}