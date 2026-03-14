package com.example.weather_forecast.data

import com.example.weather_forecast.data.models.FavouriteEntity
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun addFavourite(item: FavouriteEntity)

    suspend fun removeFavourite(lat: Double, lon: Double)

    fun getAllFavourites(): Flow<List<FavouriteEntity>>
}