package com.example.weather_forecast.utils

import com.example.weather_forecast.R

fun getWeatherGif(iconCode: String?): Int {
    return when (iconCode) {
        // Clear
        "01d" -> R.raw.ic_sun          // clear sky day
        "01n" -> R.raw.ic_moon          // clear sky night

        // Clouds
        "02d" -> R.raw.ic_cloud  // few clouds day
        "02n" -> R.raw.ic_cloud   // few clouds night
        "03d", "03n" -> R.raw.ic_scattered_clouds  // scattered clouds
        "04d", "04n" -> R.raw.ic_scattered_clouds  // broken clouds

        // Rain
        "09d", "09n" -> R.raw.ic_rain    // shower rain
        "10d" -> R.raw.ic_rain           // rain day
        "10n" -> R.raw.ic_rain           // rain night

        // Thunderstorm
        "11d", "11n" -> R.raw.ic_thunderstorm  // your angry cloud gif

        // Snow
        "13d", "13n" -> R.raw.ic_snow

        // Atmosphere (haze, fog, mist, smoke)
        "50d", "50n" -> R.raw.ic_fog

        else -> R.raw.ic_sun
    }
}