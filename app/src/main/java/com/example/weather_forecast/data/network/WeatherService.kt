package com.example.weather_forecast.data.network

import com.example.weather_forecast.data.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

        @GET("data/2.5/weather")
        suspend fun getCurrentWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): Response<WeatherResponse>

}