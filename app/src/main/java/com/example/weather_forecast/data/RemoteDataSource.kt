package com.example.weather_forecast.data

import com.example.weather_forecast.data.models.OneCallResponse
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getOneCallResponse(lat: Double, lon: Double, apiKey: String ,lang: String = "en"): Response<OneCallResponse>
    suspend fun getWeatherAlerts(lat: Double, lon: Double, apiKey: String): Response<OneCallResponse>
}