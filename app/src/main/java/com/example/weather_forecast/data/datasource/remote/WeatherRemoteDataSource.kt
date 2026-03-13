package com.example.weather_forecast.data.datasource.remote


import com.example.weather_forecast.data.models.OneCallResponse
import com.example.weather_forecast.data.network.Network
import com.example.weather_forecast.data.network.WeatherService
import retrofit2.Response

class WeatherRemoteDataSource {
    private val weatherService : WeatherService = Network.weatherService

    suspend fun getOneCallResponse(lat: Double, lon: Double, apiKey: String ,lang: String = "en"): Response<OneCallResponse> {
        return weatherService.getOneCallResponse(lat, lon, apiKey , lang = lang)
    }
    suspend fun getWeatherAlerts(lat: Double, lon: Double, apiKey: String): Response<OneCallResponse> =
        weatherService.getOneCallResponse(lat = lat, lon= lon, apiKey  = apiKey, exclude = "current,minutely,hourly,daily")

}