package com.example.weather_forecast.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {
    private const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val weatherService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}