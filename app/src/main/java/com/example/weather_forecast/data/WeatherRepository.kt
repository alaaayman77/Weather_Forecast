package com.example.weather_forecast.data

import com.example.weather_forecast.data.datasource.remote.WeatherRemoteDataSource
import com.example.weather_forecast.data.models.WeatherResponse
import retrofit2.Response

class WeatherRepository {
    private val remoteDataSource = WeatherRemoteDataSource()
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): Response<WeatherResponse> {
        return remoteDataSource.getCurrentWeather(lat, lon, apiKey)
    }
}
