package com.example.weather_forecast.data.datasource.remote


import com.example.weather_forecast.data.models.HourlyForecastResponse
import com.example.weather_forecast.data.models.WeatherResponse
import com.example.weather_forecast.data.network.Network
import com.example.weather_forecast.data.network.WeatherService
import retrofit2.Response

class WeatherRemoteDataSource {
    private val weatherService : WeatherService = Network.weatherService

    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): Response<WeatherResponse> {
        return weatherService.getCurrentWeather(lat, lon, apiKey)
    }
    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): Response<HourlyForecastResponse> {
        return weatherService.getHourlyForecast(lat , lon ,apiKey)
    }
}