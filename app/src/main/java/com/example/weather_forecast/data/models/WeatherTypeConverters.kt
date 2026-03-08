package com.example.weather_forecast.data.models
import androidx.room.TypeConverter
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