package com.example.weather_forecast.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



 @Serializable
data class WeatherAlert(
    @SerialName("sender_name")
    val senderName: String = "",
    @SerialName("event")
    val event: String = "",
    @SerialName("start")
    val start: Long = 0L,
    @SerialName("end")
    val end: Long = 0L,
    @SerialName("description")
    val description: String = "",
    @SerialName("tags")
    val tags: List<String> = emptyList()
)