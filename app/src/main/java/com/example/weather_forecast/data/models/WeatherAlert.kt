package com.example.weather_forecast.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherAlert(
    @SerializedName("sender_name")
    val senderName: String  = "",
    @SerializedName("event")
    val event: String  = "",
    @SerializedName("start")
    val start: Long    = 0L,
    @SerializedName("end")
    val end: Long    = 0L,
    @SerializedName("description")
    val description: String  = "",
    @SerializedName("tags")
    val tags: List<String> = emptyList()
)