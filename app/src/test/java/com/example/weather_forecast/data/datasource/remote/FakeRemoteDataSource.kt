package com.example.weather_forecast.data.datasource.remote

import com.example.weather_forecast.data.RemoteDataSource
import com.example.weather_forecast.data.models.OneCallResponse
import retrofit2.Response

class FakeRemoteDataSource : RemoteDataSource {
    override suspend fun getOneCallResponse(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String
    ): Response<OneCallResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherAlerts(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Response<OneCallResponse> {
        TODO("Not yet implemented")
    }
}