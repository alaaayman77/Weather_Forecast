package com.example.weather_forecast.utils

import androidx.room.TypeConverter
import com.example.weather_forecast.data.models.OneCallResponse
import kotlinx.serialization.json.Json

class WeatherTypeConverters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromOneCallResponse(response: OneCallResponse): String =
        json.encodeToString(response)

    @TypeConverter
    fun toOneCallResponse(value: String): OneCallResponse =
        json.decodeFromString(value)
}