package com.example.weather_forecast.utils

import androidx.room.TypeConverter
import com.example.weather_forecast.data.models.OneCallResponse
import kotlinx.serialization.json.Json

class WeatherTypeConverters {

    private val json = Json { ignoreUnknownKeys = true
        coerceInputValues = true}

    @TypeConverter
    fun fromOneCallResponse(response: OneCallResponse): String{
        val safe = response.copy(
            alerts = response.alerts ?: emptyList(),
            hourly = response.hourly ?: emptyList(),
            daily  = response.daily  ?: emptyList()
        )
        return json.encodeToString(OneCallResponse.serializer(), safe)
    }


    @TypeConverter
    fun toOneCallResponse(value: String): OneCallResponse =
        json.decodeFromString(OneCallResponse.serializer(), value)
}