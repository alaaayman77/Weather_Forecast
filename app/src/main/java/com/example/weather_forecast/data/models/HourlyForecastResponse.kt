package com.example.weather_forecast.data.models

data class HourlyForecastResponse(
        val cod: String,
        val cnt: Int,
        val list: List<HourlyItem>,
        val city: City
    )

    data class HourlyItem(
        val dt: Long,
        val main: HourlyMain,
        val weather: List<WeatherDescription>,
        val clouds: Clouds,
        val wind: Wind,
        val visibility: Int,
        val pop: Double,
        val rain: Rain?,
        val dt_txt: String
    ) {
        fun iconUrl() = weather.firstOrNull()?.iconUrl() ?: ""
        fun hour() = dt_txt.substring(11, 16)
    }

    data class HourlyMain(
        val temp: Double,
        val feels_like: Double,
        val temp_min: Double,
        val temp_max: Double,
        val pressure: Int,
        val humidity: Int
    )

    data class City(
        val id: Int,
        val name: String,
        val coord: Coord,
        val country: String,
        val timezone: Int,
        val sunrise: Long,
        val sunset: Long
    )
