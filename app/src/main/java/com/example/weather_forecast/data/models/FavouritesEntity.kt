package com.example.weather_forecast.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourites",
    primaryKeys = ["lat", "lon"]
)
data class FavouriteEntity(
    val lat: Double,
    val lon: Double,
    val cityName: String,
    val countryName: String,
    val countryCode: String,
    val oneCallResponse: OneCallResponse
)