package com.example.weather_forecast.data

import com.example.weather_forecast.data.datasource.remote.WeatherRemoteDataSource
import com.example.weather_forecast.data.models.OneCallResponse
import retrofit2.Response

class WeatherRepository {
    private val remoteDataSource = WeatherRemoteDataSource()
    suspend fun getOneCallResponse(lat: Double, lon: Double, apiKey: String): Response<OneCallResponse> {
        return remoteDataSource.getOneCallResponse(lat, lon, apiKey)
    }


}
